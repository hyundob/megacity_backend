package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataHgFcstGenGentDa;
import com.example.megacity_back.repository.RepDataHgFcstGenGentDaRepository;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FcstGenGentDaSyncer extends AbstractTableSyncer<RepDataHgFcstGenGentDa> {

    private final RepDataHgFcstGenGentDaRepository repo;

    @Override
    public String tableName() {
        return "REP_DATA_HG_FCST_GEN_GENT_DA";
    }

    @Override
    protected RowMapper<RepDataHgFcstGenGentDa> rowMapper() {
        return (rs, rowNum) -> RepDataHgFcstGenGentDa.builder()
                .areaGrpCd(rs.getString("AREA_GRP_CD"))
                .areaGrpId(rs.getString("AREA_GRP_ID"))
                .crtnTm(rs.getString("CRTN_TM"))
                .fcstTm(rs.getString("FCST_TM"))
                .leadTm(rs.getString("LEAD_TM"))
                .fcstProdCd(rs.getString("FCST_PROD_CD"))
                .fcstQgen(rs.getBigDecimal("FCST_QGEN"))
                .fcstCapa(rs.getBigDecimal("FCST_CAPA"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataHgFcstGenGentDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataHgFcstGenGentDa record) {
        return record.getUpdDate();
    }
}
