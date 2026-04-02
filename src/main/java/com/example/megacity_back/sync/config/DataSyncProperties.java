package com.example.megacity_back.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sync")
@Data
public class DataSyncProperties {

    private SourceDatasource sourceDatasource = new SourceDatasource();
    private int batchSize = 1000;
    private Retry retry = new Retry();
    private Lag lag = new Lag();

    @Data
    public static class SourceDatasource {
        /** JDBC URL. PostgreSQL 타임아웃은 URL 파라미터로 설정.
         *  예: jdbc:postgresql://host:5432/db?connectTimeout=5&socketTimeout=30 */
        private String url = "";
        private String username = "";
        private String password = "";
        /** HikariCP connectionTimeout (ms) - pool에서 커넥션을 얻기까지 대기 시간 */
        private long connectTimeoutMs = 5000;
        /** JdbcTemplate queryTimeout (초) - 쿼리 실행 최대 대기 시간 */
        private int socketTimeout = 30;

        public boolean isConfigured() {
            return url != null && !url.isBlank();
        }
    }

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        /** 지수 백오프 딜레이(ms): 2s → 4s → 8s */
        private long[] delaysMs = {2_000L, 4_000L, 8_000L};
    }

    @Data
    public static class Lag {
        /** 이 값(분)을 초과하면 WARN 로그 + 메트릭 알람 */
        private int alertThresholdMinutes = 10;
    }
}
