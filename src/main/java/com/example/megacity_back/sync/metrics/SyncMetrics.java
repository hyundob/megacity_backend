package com.example.megacity_back.sync.metrics;

import com.example.megacity_back.sync.config.DataSyncProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Micrometer 기반 싱크 메트릭.
 *
 * 노출 메트릭 (Actuator /actuator/prometheus 등):
 *   sync_records_synced_total{table}     - 누적 동기화 레코드 수
 *   sync_errors_total{table}             - 누적 실패 횟수
 *   sync_run_duration_seconds{table}     - 싱크 실행 시간 (Timer)
 *   sync_lag_seconds{table}              - 마지막 성공 이후 경과 시간 (Gauge)
 *   sync_lag_alert{table}                - lag > threshold → 1, 정상 → 0 (Gauge)
 */
@Slf4j
@Component
public class SyncMetrics {

    private final MeterRegistry registry;
    private final DataSyncProperties props;

    /** 테이블별 lag 기준 시각 (epoch seconds). Gauge 가 AtomicLong 을 참조. */
    private final Map<String, AtomicLong> lastSuccessEpoch = new ConcurrentHashMap<>();
    /** 테이블별 Timer */
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    /** 테이블별 성공 카운터 */
    private final Map<String, Counter> recordCounters = new ConcurrentHashMap<>();
    /** 테이블별 에러 카운터 */
    private final Map<String, Counter> errorCounters = new ConcurrentHashMap<>();

    public SyncMetrics(MeterRegistry registry, DataSyncProperties props) {
        this.registry = registry;
        this.props = props;
    }

    /** 테이블을 처음 참조할 때 메트릭 등록 (lazy) */
    private void registerIfAbsent(String table) {
        lastSuccessEpoch.computeIfAbsent(table, t -> {
            AtomicLong ref = new AtomicLong(0L);

            Gauge.builder("sync.lag.seconds", ref, AtomicLong::get)
                    .description("마지막 싱크 성공 후 경과 시간(초)")
                    .tag("table", t)
                    .register(registry);

            Gauge.builder("sync.lag.alert", ref, epochRef -> {
                        long lagSec = lagSeconds(epochRef.get());
                        long threshold = props.getLag().getAlertThresholdMinutes() * 60L;
                        return lagSec > threshold ? 1.0 : 0.0;
                    })
                    .description("lag 임계치 초과 여부 (1=초과, 0=정상)")
                    .tag("table", t)
                    .register(registry);

            return ref;
        });

        timers.computeIfAbsent(table, t ->
                Timer.builder("sync.run.duration.seconds")
                        .description("싱크 실행 시간")
                        .tag("table", t)
                        .register(registry)
        );

        recordCounters.computeIfAbsent(table, t ->
                Counter.builder("sync.records.synced.total")
                        .description("누적 동기화 레코드 수")
                        .tag("table", t)
                        .register(registry)
        );

        errorCounters.computeIfAbsent(table, t ->
                Counter.builder("sync.errors.total")
                        .description("누적 싱크 실패 횟수")
                        .tag("table", t)
                        .register(registry)
        );
    }

    /** 싱크 성공 기록 */
    public void recordSuccess(String table, int count, long durationMs) {
        registerIfAbsent(table);
        lastSuccessEpoch.get(table).set(nowEpoch());
        recordCounters.get(table).increment(count);
        timers.get(table).record(durationMs, TimeUnit.MILLISECONDS);

        long lagMin = props.getLag().getAlertThresholdMinutes();
        if (log.isDebugEnabled()) {
            log.debug("[Sync] {} 성공 - {}건, {}ms", table, count, durationMs);
        }
        // lag alert: 성공 직후는 lag = 0 이지만 이후 Gauge 가 계속 측정
    }

    /** 싱크 실패 기록 */
    public void recordError(String table) {
        registerIfAbsent(table);
        errorCounters.get(table).increment();
    }

    /**
     * 스케줄러 호출마다 lag 경보 검사.
     * lastSyncSuccessAt이 null이면 아직 싱크 미실행 → 경보 제외.
     */
    public void checkLag(String table, LocalDateTime lastSyncSuccessAt) {
        registerIfAbsent(table);
        if (lastSyncSuccessAt == null) return;

        long epoch = lastSyncSuccessAt.toEpochSecond(ZoneOffset.UTC);
        lastSuccessEpoch.get(table).set(epoch);

        long lagSeconds = lagSeconds(epoch);
        long thresholdSec = props.getLag().getAlertThresholdMinutes() * 60L;
        if (lagSeconds > thresholdSec) {
            log.warn("[Sync][LAG ALERT] 테이블={} lag={}분 (임계치={}분)",
                    table, lagSeconds / 60, props.getLag().getAlertThresholdMinutes());
        }
    }

    public Timer getTimer(String table) {
        registerIfAbsent(table);
        return timers.get(table);
    }

    private long nowEpoch() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    private long lagSeconds(long lastEpoch) {
        return Math.max(0, nowEpoch() - lastEpoch);
    }
}
