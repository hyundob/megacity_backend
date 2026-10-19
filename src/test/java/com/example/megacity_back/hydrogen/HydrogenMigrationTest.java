package com.example.megacity_back.hydrogen;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import java.sql.DriverManager;
import static org.assertj.core.api.Assertions.*;
class HydrogenMigrationTest {
    @Test void continuousSchemaCreatesThreeTablesAndValidatesCommandPercent() throws Exception {
        try(var c=DriverManager.getConnection("jdbc:h2:mem:continuous_migration;MODE=PostgreSQL","sa","")) {
            ScriptUtils.executeSqlScript(c,new FileSystemResource("db/migration/20260909_hydrogen_continuous.sql"));
            try(var s=c.createStatement();var rs=s.executeQuery("select count(*) from information_schema.tables where table_name like 'REP_DATA_HG_%'")) {
                rs.next();assertThat(rs.getInt(1)).isEqualTo(3);
            }
            assertThatThrownBy(()->c.createStatement().execute("insert into REP_DATA_HG_CONTROL(CMD_ID,CMD_TYPE,PERCENT_VALUE,EFFECTIVE_AT,RECEIVED_AT,CMD_SEQ) values('bad','REDUCE',101,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1)"))
                .isInstanceOf(java.sql.SQLException.class);
            c.createStatement().execute("insert into REP_DATA_HG_CONTROL(CMD_ID,CMD_TYPE,PERCENT_VALUE,EFFECTIVE_AT,RECEIVED_AT,CMD_SEQ) values('good','REDUCE',90,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1)");
            var configuration = new org.hibernate.cfg.Configuration()
                .addAnnotatedClass(HydrogenState.class).addAnnotatedClass(HydrogenControl.class).addAnnotatedClass(HydrogenHourly.class)
                .setProperty("hibernate.connection.url","jdbc:h2:mem:continuous_migration;MODE=PostgreSQL")
                .setProperty("hibernate.connection.username","sa").setProperty("hibernate.connection.password","")
                .setProperty("hibernate.hbm2ddl.auto","validate");
            try (var factory = configuration.buildSessionFactory()) { assertThat(factory.isOpen()).isTrue(); }
        }
    }
}
