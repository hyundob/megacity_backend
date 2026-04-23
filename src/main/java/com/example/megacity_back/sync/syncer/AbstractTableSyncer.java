package com.example.megacity_back.sync.syncer;

import com.example.megacity_back.entity.SyncCheckpoint;
import com.example.megacity_back.entity.SyncLog;
import com.example.megacity_back.repository.SyncCheckpointRepository;
import com.example.megacity_back.repository.SyncLogRepository;
import com.example.megacity_back.sync.config.DataSyncProperties;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.metrics.SyncMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 공통 증분 싱크 로직.
 *
 * 흐름:
 *   1. SYNC_CHECKPOINT 에서 lastWatermark 로드 (없으면 epoch 초기화)
 *   2. lag 경보 검사
 *   3. 소스 DB: SELECT * FROM {table} WHERE upd_date > :watermark ORDER BY upd_date ASC LIMIT :batchSize
 *   4. 대상 DB: saveAll() (JPA merge → upsert)
 *   5. SYNC_CHECKPOINT watermark 갱신, SYNC_LOG 기록
 *   6. 실패 시 지수 백오프 재시도 (2s / 4s / 8s)
 *
 * @param <T> 엔티티 타입
 */
@Slf4j
public abstract class AbstractTableSyncer<T> implements TableSyncer {

    private final SyncCheckpointRepository checkpointRepo;
    private final SyncLogRepository logRepo;
    private final SyncMetrics metrics;
    private final DataSyncProperties props;
    private final JdbcTemplate sourceJdbcTemplate;

    protected AbstractTableSyncer(SyncerContext ctx) {
        this.checkpointRepo = ctx.getCheckpointRepo();
        this.logRepo = ctx.getLogRepo();
        this.metrics = ctx.getMetrics();
        this.props = ctx.getProps();
        this.sourceJdbcTemplate = ctx.getSourceJdbcTemplate();
    }

    // ── 구현체가 제공 ──────────────────────────────────────

    /** JDBC ResultSet → 엔티티 매핑 */
    protected abstract RowMapper<T> rowMapper();

    /** 소스 레코드를 대상 DB 에 upsert */
    protected abstract void bulkUpsert(@NonNull List<T> records);

    /** 레코드에서 upd_date 추출 (watermark 갱신용) */
    protected abstract LocalDateTime extractUpdDate(T record);

    // ── 공통 실행 ──────────────────────────────────────────

    @Override
    public void sync() {
        if (sourceJdbcTemplate == null) {
            log.debug("[Sync] 소스 DB 미설정 - {} 싱크 생략", tableName());
            return;
        }

        String table = Objects.requireNonNull(tableName(), "tableName() must not return null");
        SyncCheckpoint checkpoint = checkpointRepo.findById(table)
                .orElseGet(() -> SyncCheckpoint.init(table));

        metrics.checkLag(tableName(), checkpoint.getLastSyncSuccessAt());

        LocalDateTime attemptAt = LocalDateTime.now();
        checkpoint.setLastSyncAttemptAt(attemptAt);
        checkpoint.setLastStatus("RUNNING");
        checkpointRepo.save(checkpoint);

        SyncLog syncLog = SyncLog.builder()
                .tableName(tableName())
                .startedAt(attemptAt)
                .build();

        long[] delays = props.getRetry().getDelaysMs();
        int maxAttempts = props.getRetry().getMaxAttempts();
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            syncLog.setAttemptNumber(attempt);
            long start = System.currentTimeMillis();
            try {
                SyncResult result = doSync(checkpoint.getLastWatermark(), props.getBatchSize());
                long elapsed = System.currentTimeMillis() - start;

                checkpoint.setLastWatermark(result.newWatermark());
                checkpoint.setLastSyncSuccessAt(LocalDateTime.now());
                checkpoint.setLastStatus("SUCCESS");
                checkpoint.setLastRecordCount(result.recordCount());
                checkpointRepo.save(checkpoint);

                syncLog.setStatus("SUCCESS");
                syncLog.setRecordCount(result.recordCount());
                syncLog.setNewWatermark(result.newWatermark());
                syncLog.setFinishedAt(LocalDateTime.now());
                logRepo.save(syncLog);

                metrics.recordSuccess(tableName(), result.recordCount(), elapsed);

                if (result.recordCount() > 0) {
                    log.info("[Sync] {} 완료 - {}건, {}ms, watermark={}",
                            tableName(), result.recordCount(), elapsed, result.newWatermark());
                }
                return;

            } catch (Exception e) {
                lastError = e;
                log.warn("[Sync] {} 시도 {}/{} 실패: {}", tableName(), attempt, maxAttempts, e.getMessage());
                metrics.recordError(tableName());

                if (attempt < maxAttempts) {
                    long delayMs = attempt - 1 < delays.length ? delays[attempt - 1] : delays[delays.length - 1];
                    sleepQuietly(delayMs);
                }
            }
        }

        // 모든 재시도 실패
        String errMsg = lastError != null ? lastError.getMessage() : "unknown";
        checkpoint.setLastStatus("FAILED");
        checkpointRepo.save(checkpoint);

        syncLog.setStatus("FAILED");
        syncLog.setErrorMessage(errMsg != null && errMsg.length() > 2000 ? errMsg.substring(0, 2000) : errMsg);
        syncLog.setFinishedAt(LocalDateTime.now());
        logRepo.save(syncLog);

        log.error("[Sync] {} 전체 재시도({}) 소진 - {}", tableName(), maxAttempts, errMsg);
    }

    private SyncResult doSync(LocalDateTime watermark, int batchSize) {
        String sql = String.format(
                "SELECT * FROM %s WHERE upd_date > ? ORDER BY upd_date ASC LIMIT ?",
                Objects.requireNonNull(tableName())
        );

        List<T> records = sourceJdbcTemplate.query(
                sql, Objects.requireNonNull(rowMapper()), watermark, batchSize);

        if (records.isEmpty()) {
            return new SyncResult(0, watermark);
        }

        LocalDateTime maxWatermark = records.stream()
                .map(this::extractUpdDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(watermark);

        bulkUpsert(records);

        return new SyncResult(records.size(), maxWatermark);
    }

    private void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /** 싱크 단일 배치 결과 */
    public record SyncResult(int recordCount, LocalDateTime newWatermark) {}
}
