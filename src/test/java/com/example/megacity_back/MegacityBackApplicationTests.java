package com.example.megacity_back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:context;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "sync.source.datasource.url=false", "sync.scheduling-enabled=false", "modbus.enabled=false", "kma.key="
})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
class MegacityBackApplicationTests {
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.test.web.servlet.MockMvc mvc;

    @Test
    void retiredAndUnknownRoutesReturn404InsteadOfServerError() throws Exception {
        for (String path : java.util.List.of("/api/forecast-predict/latest",
                "/api/hg-gen-predict/today", "/api/hg-gen-info/today", "/api/unknown-route")) {
            mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(path))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNotFound())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.status").value(404))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                            .value("요청한 경로를 찾을 수 없습니다."));
        }
    }
    @org.springframework.beans.factory.annotation.Autowired
    private com.example.megacity_back.repository.RepDataReKpxJejuSukubMRepository repository;

    @Test
    void preservesSourceAuditTimestampsOnInsertAndUpdate() {
        var time = java.time.LocalDateTime.of(2026, 1, 1, 0, 0);
        var row = new com.example.megacity_back.entity.RepDataReKpxJejuSukubM();
        row.setTm("202601010000");
        row.setRegDate(time);
        row.setUpdDate(time);
        repository.save(row);
        org.assertj.core.api.Assertions.assertThat(repository.findById(row.getTm()).orElseThrow().getUpdDate()).isEqualTo(time);
        row.setUpdDate(time.plusMinutes(1));
        repository.save(row);
        org.assertj.core.api.Assertions.assertThat(repository.findById(row.getTm()).orElseThrow().getUpdDate()).isEqualTo(time.plusMinutes(1));
    }

    @Test
    void contextLoads() {
    }

}
