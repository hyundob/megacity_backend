package com.example.megacity_back.sync.syncer;

/**
 * 개별 테이블 싱크 실행 단위.
 * AbstractTableSyncer 가 공통 로직(watermark, 재시도, 로그, 메트릭)을 제공하고
 * 각 구현체는 테이블명 / RowMapper / bulkUpsert 만 정의한다.
 */
public interface TableSyncer {

    /** 싱크 대상 테이블명 (SYNC_CHECKPOINT PK 와 일치) */
    String tableName();

    /**
     * 증분 싱크 1회 실행.
     * - 소스 DB 미설정 시 skip.
     * - 내부에서 재시도(최대 3회), 체크포인트 갱신, 로그/메트릭 기록.
     */
    void sync();
}
