package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataReKpxJejuSukubM;
import com.example.megacity_back.repository.RepDataReKpxJejuSukubMRepository;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KpxJejuSukubMSyncer extends AbstractTableSyncer<RepDataReKpxJejuSukubM> {

    private final RepDataReKpxJejuSukubMRepository repo;

    @Override
    public String tableName() {
        return "REP_DATA_RE_KPX_JEJU_SUKUB_M";
    }

    @Override
    protected RowMapper<RepDataReKpxJejuSukubM> rowMapper() {
        return (rs, rowNum) -> RepDataReKpxJejuSukubM.builder()
                .tm(rs.getString("TM"))
                .suppAbility(rs.getBigDecimal("SUPP_ABILITY"))
                .currPwrTot(rs.getBigDecimal("CURR_PWR_TOT"))
                .renewPwrTot(rs.getBigDecimal("RENEW_PWR_TOT"))
                .renewPwrSolar(rs.getBigDecimal("RENEW_PWR_SOLAR"))
                .renewPwrWind(rs.getBigDecimal("RENEW_PWR_WIND"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataReKpxJejuSukubM> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataReKpxJejuSukubM record) {
        return record.getUpdDate();
    }
}
