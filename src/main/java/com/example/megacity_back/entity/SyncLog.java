package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 싱크 실행 이력 로그. 각 테이블의 싱크 시도마다 한 행이 생성된다.
 */
@Entity
@Table(name = "SYNC_LOG", indexes = {
        @Index(name = "idx_sync_log_table_started", columnList = "TABLE_NAME, STARTED_AT DESC")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TABLE_NAME", length = 60, nullable = false)
    private String tableName;

    @Column(name = "STARTED_AT", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "FINISHED_AT")
    private LocalDateTime finishedAt;

    /** SUCCESS / FAILED */
    @Column(name = "STATUS", length = 10)
    private String status;

    /** 이번 배치에서 upsert된 레코드 수 */
    @Column(name = "RECORD_COUNT")
    private Integer recordCount;

    /** 이번 배치 후 새로운 watermark */
    @Column(name = "NEW_WATERMARK")
    private LocalDateTime newWatermark;

    /** 실패 시 예외 메시지 (최대 2000자) */
    @Column(name = "ERROR_MESSAGE", length = 2000)
    private String errorMessage;

    /** 재시도 포함 실제 시도 횟수 */
    @Column(name = "ATTEMPT_NUMBER")
    private Integer attemptNumber;
}
