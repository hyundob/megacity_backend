package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.repository.RepDataHgFcstNwpDaRepository;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FcstNwpDaSyncer extends AbstractTableSyncer<RepDataHgFcstNwpDa> {

    private final RepDataHgFcstNwpDaRepository repo;

    @Override
    public String tableName() {
        return "REP_DATA_HG_FCST_NWP_DA";
    }

    @Override
    protected RowMapper<RepDataHgFcstNwpDa> rowMapper() {
        return (rs, rowNum) -> RepDataHgFcstNwpDa.builder()
                .pwrExcTpCd(rs.getString("PWR_EXC_TP_CD"))
                .areaGrpCd(rs.getString("AREA_GRP_CD"))
                .areaGrpId(rs.getString("AREA_GRP_ID"))
                .crtnTm(rs.getString("CRTN_TM"))
                .fcstTm(rs.getString("FCST_TM"))
                .leadTm(rs.getString("LEAD_TM"))
                .fcstProdCd(rs.getString("FCST_PROD_CD"))
                .fcstSrad(rs.getBigDecimal("FCST_SRAD"))
                .fcstTemp(rs.getBigDecimal("FCST_TEMP"))
                .fcstHumi(rs.getBigDecimal("FCST_HUMI"))
                .fcstWspd(rs.getBigDecimal("FCST_WSPD"))
                .fcstWdir(rs.getBigDecimal("FCST_WDIR"))
                .fcstWsl2(rs.getBigDecimal("FCST_WSL2"))
                .fcstWdl2(rs.getBigDecimal("FCST_WDL2"))
                .fcstPsfc(rs.getBigDecimal("FCST_PSFC"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataHgFcstNwpDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataHgFcstNwpDa record) {
        return record.getUpdDate();
    }
}
