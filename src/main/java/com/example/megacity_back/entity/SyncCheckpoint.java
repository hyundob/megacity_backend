package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 테이블별 증분 싱크 watermark를 저장한다.
 * lastWatermark = 소스 DB에서 마지막으로 동기화한 레코드의 max(upd_date)
 */
@Entity
@Table(name = "SYNC_CHECKPOINT")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncCheckpoint {

    /** 대상 테이블명 (PK) */
    @Id
    @Column(name = "TABLE_NAME", length = 60, nullable = false)
    private String tableName;

    /**
     * 소스 DB max(upd_date) 기준 watermark.
     * 다음 싱크 쿼리: WHERE upd_date > lastWatermark ORDER BY upd_date ASC LIMIT batchSize
     */
    @Column(name = "LAST_WATERMARK")
    private LocalDateTime lastWatermark;

    /** 마지막 싱크 성공 시각 (lag 계산 기준) */
    @Column(name = "LAST_SYNC_SUCCESS_AT")
    private LocalDateTime lastSyncSuccessAt;

    /** 마지막 싱크 시도 시각 */
    @Column(name = "LAST_SYNC_ATTEMPT_AT")
    private LocalDateTime lastSyncAttemptAt;

    /** SUCCESS / FAILED / RUNNING */
    @Column(name = "LAST_STATUS", length = 10)
    private String lastStatus;

    /** 마지막 싱크 배치에서 처리한 레코드 수 */
    @Column(name = "LAST_RECORD_COUNT")
    private Integer lastRecordCount;

    /** 최초 체크포인트 생성 - 전체 이력을 가져오기 위해 watermark를 epoch로 설정 */
    public static SyncCheckpoint init(String tableName) {
        return SyncCheckpoint.builder()
                .tableName(tableName)
                .lastWatermark(LocalDateTime.of(1970, 1, 1, 0, 0, 0))
                .lastStatus("INIT")
                .build();
    }
}
