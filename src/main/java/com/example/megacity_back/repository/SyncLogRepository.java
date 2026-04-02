package com.example.megacity_back.repository;

import com.example.megacity_back.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    /** 특정 테이블의 최근 N건 로그 조회 */
    List<SyncLog> findTop20ByTableNameOrderByStartedAtDesc(String tableName);
}
