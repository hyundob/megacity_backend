package com.example.megacity_back.sync;

import com.example.megacity_back.entity.SyncCheckpoint;
import com.example.megacity_back.repository.SyncCheckpointRepository;
import com.example.megacity_back.repository.SyncLogRepository;
import com.example.megacity_back.sync.config.DataSyncProperties;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.metrics.SyncMetrics;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SyncBoundaryTest {
    private JdbcTemplate source;
    private final Map<Integer, Row> target = new HashMap<>();
    private final SyncCheckpoint checkpoint = SyncCheckpoint.init("source_rows");
    private final LocalDateTime timestamp = LocalDateTime.of(2026, 9, 8, 10, 0);
    private AbstractTableSyncer<Row> syncer;
    private boolean failSecondWrite;
    private int writes;

    record Row(int id, LocalDateTime updatedAt) {}

    @BeforeEach
    void setup() {
        source = new JdbcTemplate(new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "sa", ""));
        source.execute("CREATE TABLE source_rows (id INT PRIMARY KEY, upd_date TIMESTAMP NOT NULL)");
        var checkpoints = mock(SyncCheckpointRepository.class);
        when(checkpoints.findById("source_rows")).thenReturn(Optional.of(checkpoint));
        var props = new DataSyncProperties();
        props.setBatchSize(10);
        props.getRetry().setMaxAttempts(1);
        var context = new SyncerContext(checkpoints, mock(SyncLogRepository.class), mock(SyncMetrics.class), props, source);
        syncer = new AbstractTableSyncer<>(context) {
            public String tableName() { return "source_rows"; }
            protected RowMapper<Row> rowMapper() {
                return (rs, index) -> new Row(rs.getInt("id"), rs.getObject("upd_date", LocalDateTime.class));
            }
            protected void bulkUpsert(List<Row> records) {
                if (++writes == 2 && failSecondWrite) throw new IllegalStateException("Target unavailable");
                records.forEach(row -> target.put(row.id(), row));
            }
            protected LocalDateTime extractUpdDate(Row row) { return row.updatedAt(); }
        };
    }

    private void insert(int id, LocalDateTime time) { source.update("INSERT INTO source_rows VALUES (?, ?)", id, time); }

    @Test
    void doesNotLoseTiesLargerThanBatchSize() {
        for (int i = 0; i < 35; i++) insert(i, timestamp);
        insert(35, timestamp.plusMinutes(1));
        syncer.sync();
        assertThat(target).hasSize(36);
        assertThat(checkpoint.getLastWatermark()).isEqualTo(timestamp.plusMinutes(1));
        syncer.sync();
        assertThat(target).hasSize(36);
    }

    @Test
    void replaysPersistedBoundaryIncludingLateTiedRows() {
        insert(1, timestamp);
        syncer.sync();
        insert(2, timestamp);
        syncer.sync();
        assertThat(target).containsKeys(1, 2);
        insert(3, timestamp);
        insert(4, timestamp.plusMinutes(1));
        syncer.sync();
        assertThat(target).hasSize(4);
    }

    @Test
    void partialTargetFailureDoesNotAdvanceCheckpointAndCanRecover() {
        for (int i = 0; i < 25; i++) insert(i, timestamp);
        failSecondWrite = true;
        syncer.sync();
        assertThat(target).hasSize(10);
        assertThat(checkpoint.getLastStatus()).isEqualTo("FAILED");
        assertThat(checkpoint.getLastWatermark()).isBefore(timestamp);
        failSecondWrite = false;
        syncer.sync();
        assertThat(target).hasSize(25);
        assertThat(checkpoint.getLastStatus()).isEqualTo("SUCCESS");
    }

    @Test
    void emptySourceKeepsWatermark() {
        var before = checkpoint.getLastWatermark();
        syncer.sync();
        assertThat(checkpoint.getLastWatermark()).isEqualTo(before);
        assertThat(checkpoint.getLastRecordCount()).isZero();
    }
}
