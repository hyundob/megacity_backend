package com.example.megacity_back.hydrogen;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.time.*;
import java.util.concurrent.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={
    "spring.datasource.url=jdbc:h2:mem:hydrogen;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa","spring.datasource.password=","spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect","spring.jpa.hibernate.ddl-auto=create-drop",
    "sync.source.datasource.url=false","sync.scheduling-enabled=false","modbus.enabled=false","kma.key=",
    "hydrogen.scheduling-enabled=false","hydrogen.command-api-key=test-command-key"
}) @AutoConfigureMockMvc
class HydrogenIntegrationTest {
    @Autowired HydrogenContinuousService service;
    @Autowired HydrogenStateRepository states;
    @Autowired HydrogenHourlyRepository hours;
    @Autowired HydrogenControlRepository controls;
    @Autowired MockMvc mvc;
    @MockitoBean(name="hydrogenClock") Clock clock;
    final LocalDateTime start=LocalDateTime.of(2026,9,10,0,0);
    @BeforeEach void setup() {hours.deleteAll();controls.deleteAll();states.deleteAll();setNow(start);}
    void setNow(LocalDateTime now) {when(clock.instant()).thenReturn(now.atZone(HydrogenTime.ZONE).toInstant());}
    HydrogenContinuousService.Request request(String id,String type,Integer percent) {
        return new HydrogenContinuousService.Request(id,type,percent==null?null:BigDecimal.valueOf(percent),null);
    }
    @Test void initializesAutomaticallyAndPersistsEachClosedHourOnce() {
        service.initialize();service.initialize();assertThat(states.count()).isEqualTo(1);
        service.tick();assertThat(hours.count()).isZero();
        setNow(start.plusDays(1));service.tick();service.tick();
        assertThat(hours.count()).isEqualTo(288);
        var history=service.history(null,null,1);
        assertThat(history.hours().stream().mapToDouble(h->h.getProductionKg().doubleValue()).sum()).isCloseTo(500,within(1e-6));
        assertThat(history.hours().stream().mapToDouble(h->h.getEnergyKwh().doubleValue()).sum()).isCloseTo(500/0.115,within(1e-6));
        double refilled=history.hours().stream().mapToDouble(h->h.getRefilledBiogasNm3().doubleValue()).sum();
        assertThat(history.status().state().getStoredBiogasNm3().doubleValue()).isCloseTo(refilled,within(1e-6));
        assertThat(history.status().state().getStoredBiogasNm3().doubleValue()).isBetween(2000.0,20000.0);
        assertThat(history.status().state().getStoredHydrogenKg().doubleValue()).isCloseTo(500,within(1e-6));
    }
    @Test void startupMidHourOnlyCalculatesTimeSinceActualStartup() {
        setNow(start.plusMinutes(2));service.initialize();setNow(start.plusMinutes(5));service.tick();
        var h=hours.findAll().get(0);assertThat(h.getDurationSeconds()).isEqualTo(180);
        assertThat(h.getProductionKg().doubleValue()).isCloseTo(500.0/24*3/60,within(1e-8));
        assertThat(h.getTm()).isEqualTo("202609100000");
    }
    @Test void stopReduceAndNormalPersistUntilNextInstruction() {
        service.initialize();setNow(start.plusMinutes(30));service.receive(request("stop","STOP",null));
        setNow(start.plusHours(1));service.tick();service.receive(request("reduce","REDUCE",90));
        setNow(start.plusHours(2));service.tick();service.receive(request("normal","NORMAL",null));
        setNow(start.plusHours(3));service.tick();
        var h=service.history(null,null,1).hours();assertThat(h).hasSize(36);
        assertThat(h.get(6).getStopSeconds()).isEqualTo(300);
        assertThat(h.get(12).getProductionKg().doubleValue()).isCloseTo(500.0/288*0.1,within(1e-8));
        assertThat(h.get(24).getProductionKg().doubleValue()).isCloseTo(500.0/288,within(1e-8));
    }
    @Test void stoppedHoursStillHaveRowsAndInventoriesAreCarriedForward() {
        service.initialize();service.receive(request("stop","STOP",null));setNow(start.plusDays(1));service.tick();
        assertThat(hours.count()).isEqualTo(288);
        assertThat(hours.findAll()).allSatisfy(h->{assertThat(h.getProductionKg()).isZero();assertThat(h.getEnergyKwh()).isZero();assertThat(h.getStopSeconds()).isEqualTo(300);});
    }
    @Test void sameCommandIsIdempotentAndConflictingRetryRejected() {
        service.initialize();var q=request("retry","REDUCE",90);var first=service.receive(q);
        setNow(start.plusMinutes(10));var again=service.receive(q);assertThat(again.getEffectiveAt()).isEqualTo(first.getEffectiveAt());assertThat(controls.count()).isEqualTo(1);
        assertThatThrownBy(()->service.receive(request("retry","REDUCE",80))).isInstanceOf(HydrogenProblem.class);
    }
    @Test void simultaneousCommandsHaveDeterministicLastReceivedPriority() {
        service.initialize();service.receive(request("a","STOP",null));service.receive(request("b","PLUS_DR",50));
        setNow(start.plusHours(1));service.tick();assertThat(hours.findAll().get(0).getProductionKg().doubleValue()).isCloseTo((500.0/24+60)/2/12,within(1e-8));
    }
    @Test void futureCommandAppliesAtItsExactBoundaryAndInvalidInputsDoNotWrite() {
        service.initialize();service.receive(new HydrogenContinuousService.Request("future","STOP",null,start.plusMinutes(30)));
        assertThatThrownBy(()->service.receive(request("bad","REDUCE",101))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->service.receive(request("bad","STOP",0))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->service.receive(new HydrogenContinuousService.Request("past","STOP",null,start.minusSeconds(1)))).isInstanceOf(IllegalArgumentException.class);
        setNow(start.plusHours(1));service.tick();assertThat(service.history(null,null,1).hours().get(6).getStopSeconds()).isEqualTo(300);
    }
    @Test void restartContinuesCursorAndFullTankIsExchangedWithoutDailyReset() {
        service.initialize();setNow(start.plusDays(3));service.tick();service.initialize();service.tick();service.tick();service.tick();
        assertThat(states.count()).isEqualTo(1);assertThat(hours.count()).isEqualTo(864);
        assertThat(hours.findAll().stream().mapToInt(HydrogenHourly::getTankExchanges).sum()).isEqualTo(1);
        assertThat(states.findById("MAIN").orElseThrow().getStoredHydrogenKg().doubleValue()).isCloseTo(0,within(1e-6));
    }
    @Test void concurrentTicksDoNotDuplicateHours() throws Exception {
        service.initialize();setNow(start.plusHours(2));
        var executor=Executors.newFixedThreadPool(2);
        try {
            var a=executor.submit(()->service.tick());var b=executor.submit(()->service.tick());a.get(30,TimeUnit.SECONDS);b.get(30,TimeUnit.SECONDS);
        } finally { executor.shutdownNow(); }
        assertThat(hours.count()).isEqualTo(24);
    }
    @Test void apiExposesOnlyAutomaticHistoryAndAuthenticatedCommands() throws Exception {
        service.initialize();
        mvc.perform(get("/api/hydrogen/history")).andExpect(status().isOk()).andExpect(jsonPath("$.status.state.systemId").value("MAIN"));
        mvc.perform(get("/api/hydrogen/history").param("days","32")).andExpect(status().isBadRequest());
        var json="{\"commandId\":\"api-stop\",\"type\":\"STOP\"}";
        mvc.perform(post("/api/hydrogen/commands").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/hydrogen/commands").header("X-Hydrogen-Command-Key","test-command-key").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk());
        mvc.perform(post("/api/hydrogen/runs").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotFound());
    }
    @Test void fiveMinuteBoundaryAndMidIntervalCommandAreExact() {
        service.initialize();setNow(start.plusMinutes(2));service.receive(request("reduce-mid","REDUCE",50));
        setNow(start.plusMinutes(4).plusSeconds(59));service.tick();assertThat(hours.count()).isZero();
        assertThat(service.history(null,null,1).status().nextSaveAt()).isEqualTo(start.plusMinutes(5));
        setNow(start.plusMinutes(5));service.tick();service.tick();
        var result=service.history(start,start.plusMinutes(5),1);
        assertThat(result.hours()).hasSize(1);
        var row=result.hours().get(0);
        assertThat(row.getDurationSeconds()).isEqualTo(300);
        assertThat(row.getReducedSeconds()).isEqualTo(180);
        assertThat(row.getProductionKg().doubleValue()).isCloseTo(500.0/24*(2.0/60+3.0/60*0.5),within(1e-8));
        assertThatThrownBy(()->service.history(start,start.plusMinutes(6),1)).isInstanceOf(IllegalArgumentException.class);
        assertThat(service.history(null,null,1).hours()).hasSize(1);
    }
    @Test void legacyHourlyIntervalIsPreservedAndNewRowsStartAtItsEnd() {
        service.initialize();setNow(start.plusHours(1));service.tick();
        var old=service.history(null,null,1).hours();
        var legacy=old.get(0);
        hours.deleteAll(old.subList(1,old.size()));
        legacy.setIntervalEnd(start.plusHours(1));legacy.setDurationSeconds(3600);
        legacy.setProductionKg(new BigDecimal("20.833333333"));hours.save(legacy);
        setNow(start.plusHours(1).plusMinutes(10));service.initialize();service.tick();
        var result=service.history(start,start.plusHours(1).plusMinutes(10),1).hours();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDurationSeconds()).isEqualTo(3600);
        assertThat(result.get(1).getIntervalStart()).isEqualTo(result.get(0).getIntervalEnd());
        assertThat(result.get(1).getTm()).isEqualTo("202609100100");
        assertThat(result.get(2).getTm()).isEqualTo("202609100105");
        assertThat(result.get(2).getDurationSeconds()).isEqualTo(300);
    }
    @Test void fullPlusDrUsesSixtyKgAndExactPoint115Conversion() {
        service.initialize();
        var old=states.findById("MAIN").orElseThrow();old.setEnergyPerKg(new BigDecimal("8.7"));states.save(old);
        service.initialize();
        assertThat(states.findById("MAIN").orElseThrow().getEnergyPerKg().doubleValue()).isCloseTo(1/0.115,within(1e-9));
        assertThatThrownBy(()->service.receive(request("too-high","PLUS_DR",101))).isInstanceOf(IllegalArgumentException.class);
        service.receive(request("full","PLUS_DR",100));setNow(start.plusMinutes(5));service.tick();
        var h=service.history(null,null,1).hours().get(0);
        assertThat(h.getProductionKg().doubleValue()).isEqualTo(5);
        assertThat(h.getEnergyKwh().doubleValue()).isCloseTo(5/0.115,within(1e-9));
        service.receive(request("half-reduce","REDUCE",50));setNow(start.plusMinutes(10));service.tick();
        assertThat(service.history(null,null,1).hours().get(1).getProductionKg().doubleValue()).isCloseTo(500.0/288*0.5,within(1e-9));
    }
    @Test void commandCorsAcceptsConfiguredOrigin() throws Exception {
        mvc.perform(options("/api/hydrogen/commands").header("Origin","http://localhost:3000").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","content-type,x-hydrogen-command-key"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:3000"));
        mvc.perform(options("/api/hydrogen/commands").header("Origin","http://unknown.example").header("Access-Control-Request-Method","POST"))
            .andExpect(status().isForbidden());
    }
}
