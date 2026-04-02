package com.example.megacity_back.sync;

import com.example.megacity_back.sync.syncer.TableSyncer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 1분 주기 데이터 싱크 스케줄러.
 *
 * - 매 분 정각(0초)에 실행
 * - 7개 테이블을 순차 처리 (중첩 실행 방지)
 * - 소스 DB 미설정 시 각 syncer 가 skip 로그만 출력
 *
 * 운영 파라미터 (application.properties):
 *   sync.batch-size=1000
 *   sync.retry.max-attempts=3
 *   sync.retry.delays-ms=2000,4000,8000
 *   sync.lag.alert-threshold-minutes=10
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncScheduler {

    private final List<TableSyncer> syncers;

    /**
     * 매 분 0초에 전체 테이블 증분 싱크 실행.
     * Spring 기본 단일 스레드 스케줄러 사용 → 이전 실행이 끝나지 않으면 다음 트리거 대기.
     */
    @Scheduled(cron = "0 * * * * *")
    public void syncAll() {
        log.debug("[Sync] 스케줄 실행 시작 - {}", LocalDateTime.now());
        for (TableSyncer syncer : syncers) {
            try {
                syncer.sync();
            } catch (Exception e) {
                // 개별 syncer 예외가 전체 스케줄을 중단하지 않도록 격리
                log.error("[Sync] {} 처리 중 예상치 못한 예외: {}", syncer.tableName(), e.getMessage(), e);
            }
        }
        log.debug("[Sync] 스케줄 실행 완료 - {}", LocalDateTime.now());
    }

    /**
     * 테스트 또는 수동 실행용. 특정 테이블만 즉시 싱크.
     */
    public void syncTable(String tableName) {
        syncers.stream()
                .filter(s -> s.tableName().equalsIgnoreCase(tableName))
                .findFirst()
                .ifPresentOrElse(
                        TableSyncer::sync,
                        () -> log.warn("[Sync] 테이블 '{}' 에 해당하는 syncer 없음", tableName)
                );
    }
}
