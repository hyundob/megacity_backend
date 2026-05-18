package com.example.megacity_back.sync.config;

import com.example.megacity_back.repository.SyncCheckpointRepository;
import com.example.megacity_back.repository.SyncLogRepository;
import com.example.megacity_back.sync.metrics.SyncMetrics;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SyncerContext {

    private final SyncCheckpointRepository checkpointRepo;
    private final SyncLogRepository logRepo;
    private final SyncMetrics metrics;
    private final DataSyncProperties props;
    @Nullable
    private final JdbcTemplate sourceJdbcTemplate;

    public SyncerContext(
            SyncCheckpointRepository checkpointRepo,
            SyncLogRepository logRepo,
            SyncMetrics metrics,
            DataSyncProperties props,
            @Nullable @Qualifier("sourceJdbcTemplate") JdbcTemplate sourceJdbcTemplate) {
        this.checkpointRepo = checkpointRepo;
        this.logRepo = logRepo;
        this.metrics = metrics;
        this.props = props;
        this.sourceJdbcTemplate = sourceJdbcTemplate;
    }
}
