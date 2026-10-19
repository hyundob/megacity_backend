package com.example.megacity_back.sync;

import com.example.megacity_back.sync.config.DataSyncProperties;
import com.example.megacity_back.sync.metrics.SyncMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.*;

class SyncMetricsTest {
    @Test
    void lagIsElapsedSecondsAndUsesKoreanTimestamp() {
        var registry = new SimpleMeterRegistry();
        var metrics = new SyncMetrics(registry, new DataSyncProperties());
        metrics.checkLag("table", LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusSeconds(120));
        assertThat(registry.get("sync.lag.seconds").gauge().value()).isBetween(119.0, 125.0);
        metrics.recordSuccess("table", 0, 1);
        assertThat(registry.get("sync.lag.seconds").gauge().value()).isBetween(0.0, 2.0);
    }
}
