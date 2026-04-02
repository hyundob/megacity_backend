package com.example.megacity_back.sync;

import com.example.megacity_back.entity.SyncCheckpoint;
import com.example.megacity_back.entity.SyncLog;
import com.example.megacity_back.repository.SyncCheckpointRepository;
import com.example.megacity_back.repository.SyncLogRepository;
import com.example.megacity_back.repository.RepDataReKpxJejuSukubMRepository;
import com.example.megacity_back.sync.syncer.impl.KpxJejuSukubMSyncer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 데이터 싱크 통합 테스트.
 *
 * 두 개의 PostgreSQL Testcontainer를 사용:
 *   - targetContainer: 로컬(대상) DB → spring.datasource
 *   - sourceContainer: 소스(원격) DB → sync.source.datasource
 *
 * 검증 시나리오:
 *   1. 정상 싱크 - 소스 데이터가 대상으로 복사됨
 *   2. 멱등성 - 동일 데이터 2회 싱크 후 중복 없음
 *   3. 증분 싱크 - watermark 이후 추가된 데이터만 가져옴
 *   4. 중단 복구 - watermark가 남아있어 재시작 후 중단 지점부터 재개
 *   5. SyncLog 기록 - 성공/실패 로그가 기록됨
 */
@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = DataSyncIntegrationTest.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DataSyncIntegrationTest {

    // ── Testcontainers ───────────────────────────────────────────────────────

    @Container
    static final PostgreSQLContainer<?> targetContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("megacitydb")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    static final PostgreSQLContainer<?> sourceContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("sourcedb")
                    .withUsername("test")
                    .withPassword("test");

    /**
     * 두 컨테이너의 JDBC URL 을 Spring 프로퍼티에 주입.
     */
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + targetContainer.getJdbcUrl(),
                    "spring.datasource.username=" + targetContainer.getUsername(),
                    "spring.datasource.password=" + targetContainer.getPassword(),
                    "sync.source.datasource.url=" + sourceContainer.getJdbcUrl(),
                    "sync.source.datasource.username=" + sourceContainer.getUsername(),
                    "sync.source.datasource.password=" + sourceContainer.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=create-drop",
                    "sync.batch-size=10",
                    "sync.retry.max-attempts=3",
                    "sync.retry.delays-ms=100,200,400",
                    "sync.lag.alert-threshold-minutes=10"
            ).applyTo(ctx.getEnvironment());
        }
    }

    // ── 주입 ─────────────────────────────────────────────────────────────────

    @Autowired
    private KpxJejuSukubMSyncer syncer;

    @Autowired
    private RepDataReKpxJejuSukubMRepository targetRepo;

    @Autowired
    private SyncCheckpointRepository checkpointRepo;

    @Autowired
    private SyncLogRepository logRepo;

    @Autowired
    @Qualifier("sourceJdbcTemplate")
    private JdbcTemplate sourceJdbc;

    // ── Setup ────────────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        // 소스 DB 테이블 생성 (대상 DB 는 JPA ddl-auto 로 자동 생성)
        sourceJdbc.execute("""
                CREATE TABLE IF NOT EXISTS REP_DATA_RE_KPX_JEJU_SUKUB_M (
                    TM              VARCHAR(12) PRIMARY KEY,
                    SUPP_ABILITY    NUMERIC(18,5),
                    CURR_PWR_TOT    NUMERIC(18,5),
                    RENEW_PWR_TOT   NUMERIC(18,5),
                    RENEW_PWR_SOLAR NUMERIC(18,5),
                    RENEW_PWR_WIND  NUMERIC(18,5),
                    REG_DATE        TIMESTAMP NOT NULL,
                    UPD_DATE        TIMESTAMP NOT NULL
                )
                """);
        // 매 테스트 전 초기화
        sourceJdbc.execute("DELETE FROM REP_DATA_RE_KPX_JEJU_SUKUB_M");
        targetRepo.deleteAll();
        checkpointRepo.deleteAll();
        logRepo.deleteAll();
    }

    // ── 시나리오 1: 정상 싱크 ────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("소스 데이터가 대상으로 정상 복사된다")
    void normalSync() {
        LocalDateTime updDate = LocalDateTime.now().minusMinutes(5);
        insertSourceRow("202401010000", updDate);
        insertSourceRow("202401010001", updDate);

        syncer.sync();

        assertThat(targetRepo.count()).isEqualTo(2);
        assertThat(logRepo.findTop20ByTableNameOrderByStartedAtDesc("REP_DATA_RE_KPX_JEJU_SUKUB_M"))
                .hasSize(1)
                .first()
                .satisfies(log -> {
                    assertThat(log.getStatus()).isEqualTo("SUCCESS");
                    assertThat(log.getRecordCount()).isEqualTo(2);
                });
    }

    // ── 시나리오 2: 멱등성 ───────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("동일 데이터 2회 싱크해도 중복 없이 upsert 된다")
    void idempotentSync() {
        LocalDateTime updDate = LocalDateTime.now().minusMinutes(5);
        insertSourceRow("202401020000", updDate);

        syncer.sync();
        syncer.sync(); // 두 번째 실행 - watermark 이후 데이터 없음

        assertThat(targetRepo.count()).isEqualTo(1);
        // 두 번째 싱크는 0건이므로 로그 기록 없음 (또는 SUCCESS + recordCount=0)
        List<SyncLog> logs = logRepo.findTop20ByTableNameOrderByStartedAtDesc("REP_DATA_RE_KPX_JEJU_SUKUB_M");
        assertThat(logs).hasSizeGreaterThanOrEqualTo(1);
        assertThat(logs.get(0).getStatus()).isEqualTo("SUCCESS");
    }

    // ── 시나리오 3: 증분 싱크 ────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("watermark 이후 추가된 데이터만 싱크된다")
    void incrementalSync() {
        LocalDateTime firstBatch = LocalDateTime.now().minusMinutes(10);
        insertSourceRow("202401030000", firstBatch);
        insertSourceRow("202401030001", firstBatch);

        syncer.sync();
        assertThat(targetRepo.count()).isEqualTo(2);

        // watermark 이후 새로운 데이터 추가
        LocalDateTime secondBatch = firstBatch.plusMinutes(5);
        insertSourceRow("202401030002", secondBatch);

        syncer.sync();
        assertThat(targetRepo.count()).isEqualTo(3);

        SyncCheckpoint checkpoint = checkpointRepo.findById("REP_DATA_RE_KPX_JEJU_SUKUB_M").orElseThrow();
        assertThat(checkpoint.getLastWatermark()).isAfterOrEqualTo(firstBatch);
    }

    // ── 시나리오 4: 중단 복구 ────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("중단 후 재시작 시 watermark 기준으로 나머지를 이어서 처리한다")
    void resumeAfterInterruption() {
        LocalDateTime t1 = LocalDateTime.now().minusMinutes(20);
        LocalDateTime t2 = t1.plusMinutes(5);

        insertSourceRow("202401040000", t1);
        insertSourceRow("202401040001", t1);

        syncer.sync();
        long countAfterFirst = targetRepo.count();
        assertThat(countAfterFirst).isEqualTo(2);

        // t2 이후 데이터 추가 후 애플리케이션 재시작 시뮬레이션 (checkpoint 는 유지됨)
        insertSourceRow("202401040002", t2);
        insertSourceRow("202401040003", t2);

        syncer.sync();
        assertThat(targetRepo.count()).isEqualTo(4);
    }

    // ── 시나리오 5: SyncLog 실패 기록 ────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("소스 테이블이 없을 경우 FAILED 로그가 기록된다")
    void failureLogged() {
        // 소스 테이블 삭제하여 강제 실패
        sourceJdbc.execute("DROP TABLE IF EXISTS REP_DATA_RE_KPX_JEJU_SUKUB_M");

        syncer.sync();

        List<SyncLog> logs = logRepo.findTop20ByTableNameOrderByStartedAtDesc("REP_DATA_RE_KPX_JEJU_SUKUB_M");
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getStatus()).isEqualTo("FAILED");
        assertThat(logs.get(0).getErrorMessage()).isNotBlank();
        assertThat(logs.get(0).getAttemptNumber()).isEqualTo(3); // 3회 재시도

        SyncCheckpoint checkpoint = checkpointRepo.findById("REP_DATA_RE_KPX_JEJU_SUKUB_M").orElseThrow();
        assertThat(checkpoint.getLastStatus()).isEqualTo("FAILED");

        // 테이블 재생성 (다른 테스트 영향 방지)
        sourceJdbc.execute("""
                CREATE TABLE IF NOT EXISTS REP_DATA_RE_KPX_JEJU_SUKUB_M (
                    TM VARCHAR(12) PRIMARY KEY,
                    SUPP_ABILITY NUMERIC(18,5), CURR_PWR_TOT NUMERIC(18,5),
                    RENEW_PWR_TOT NUMERIC(18,5), RENEW_PWR_SOLAR NUMERIC(18,5),
                    RENEW_PWR_WIND NUMERIC(18,5),
                    REG_DATE TIMESTAMP NOT NULL, UPD_DATE TIMESTAMP NOT NULL
                )
                """);
    }

    // ── 헬퍼 ─────────────────────────────────────────────────────────────────

    private void insertSourceRow(String tm, LocalDateTime updDate) {
        sourceJdbc.update("""
                INSERT INTO REP_DATA_RE_KPX_JEJU_SUKUB_M
                    (TM, SUPP_ABILITY, CURR_PWR_TOT, RENEW_PWR_TOT,
                     RENEW_PWR_SOLAR, RENEW_PWR_WIND, REG_DATE, UPD_DATE)
                VALUES (?, 1000.0, 800.0, 300.0, 200.0, 100.0, ?, ?)
                """,
                tm, updDate, updDate);
    }
}
