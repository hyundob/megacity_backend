package com.example.megacity_back.sync;

import com.example.megacity_back.sync.config.DataSyncProperties;
import com.example.megacity_back.sync.config.SourceDataSourceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.*;

class SourceConfigurationTest {
    @Test
    void documentedSourcePropertiesActuallyConfigureTheSourceConnection() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
                .withUserConfiguration(DataSyncProperties.class, SourceDataSourceConfig.class)
                .withPropertyValues("sync.source.datasource.url=jdbc:h2:mem:binding",
                        "sync.source.datasource.username=sa", "sync.source.datasource.password=",
                        "sync.source.datasource.socket-timeout=7")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var jdbc = context.getBean("sourceJdbcTemplate", JdbcTemplate.class);
                    assertThat(jdbc.getQueryTimeout()).isEqualTo(7);
                    assertThat(jdbc.queryForObject("SELECT 1", Integer.class)).isEqualTo(1);
                });
    }
}
