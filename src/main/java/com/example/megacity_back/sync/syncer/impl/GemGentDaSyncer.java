package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataHgMeasGemGentDa;
import com.example.megacity_back.repository.RepDataHgMeasGemGentDaRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GemGentDaSyncer extends AbstractTableSyncer<RepDataHgMeasGemGentDa> {

    private final RepDataHgMeasGemGentDaRepository repo;

    public GemGentDaSyncer(SyncerContext ctx, RepDataHgMeasGemGentDaRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_HG_MEAS_GEM_GENT_DA";
    }

    @Override
    protected RowMapper<RepDataHgMeasGemGentDa> rowMapper() {
        return (rs, rowNum) -> RepDataHgMeasGemGentDa.builder()
                .tm(rs.getString("TM"))
                .areaGrpCd(rs.getString("AREA_GRP_CD"))
                .areaGrpId(rs.getString("AREA_GRP_ID"))
                .hgenProd(rs.getBigDecimal("HGEN_PROD"))
                .hgenCapa(rs.getBigDecimal("HGEN_CAPA"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataHgMeasGemGentDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataHgMeasGemGentDa record) {
        return record.getUpdDate();
    }
}
