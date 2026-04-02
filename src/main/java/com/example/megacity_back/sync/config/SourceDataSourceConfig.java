package com.example.megacity_back.sync.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * 소스 DB DataSource & JdbcTemplate 빈 등록.
 * sync.source.datasource.url 이 설정된 경우에만 활성화된다.
 *
 * PostgreSQL 타임아웃 설정 방법:
 *   sync.source.datasource.url=jdbc:postgresql://host:5432/db?connectTimeout=5&socketTimeout=30
 *   sync.source.datasource.connect-timeout-ms=5000   (HikariCP pool 획득 대기)
 *   sync.source.datasource.socket-timeout=30          (JdbcTemplate 쿼리 타임아웃, 초)
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sync.source.datasource.url", matchIfMissing = false)
public class SourceDataSourceConfig {

    private final DataSyncProperties props;

    @Bean("sourceDataSource")
    public DataSource sourceDataSource() {
        DataSyncProperties.SourceDatasource src = props.getSourceDatasource();
        log.info("[Sync] Source DataSource 초기화: url={}", src.getUrl());

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(src.getUrl());
        config.setUsername(src.getUsername());
        config.setPassword(src.getPassword());
        config.setConnectionTimeout(src.getConnectTimeoutMs());
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setPoolName("sync-source-pool");
        config.setReadOnly(true);

        return new HikariDataSource(config);
    }

    @Bean("sourceJdbcTemplate")
    public JdbcTemplate sourceJdbcTemplate(@Qualifier("sourceDataSource") DataSource sourceDataSource) {
        JdbcTemplate template = new JdbcTemplate(Objects.requireNonNull(sourceDataSource));
        template.setQueryTimeout(props.getSourceDatasource().getSocketTimeout());
        return template;
    }
}
