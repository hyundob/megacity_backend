package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataReFcstLfdWa;
import com.example.megacity_back.repository.RepDataReFcstLfdWaRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FcstLfdWaSyncer extends AbstractTableSyncer<RepDataReFcstLfdWa> {

    private final RepDataReFcstLfdWaRepository repo;

    public FcstLfdWaSyncer(SyncerContext ctx, RepDataReFcstLfdWaRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_RE_FCST_LFD_WA";
    }

    @Override
    protected RowMapper<RepDataReFcstLfdWa> rowMapper() {
        return (rs, rowNum) -> RepDataReFcstLfdWa.builder()
                .crtnTm(rs.getString("CRTN_TM"))
                .fcstTm(rs.getString("FCST_TM"))
                .leadTm(rs.getString("LEAD_TM"))
                .fcstProdCd(rs.getString("FCST_PROD_CD"))
                .fcstQg01(rs.getBigDecimal("FCST_QG01"))
                .fcstQg02(rs.getBigDecimal("FCST_QG02"))
                .fcstQg03(rs.getBigDecimal("FCST_QG03"))
                .fcstQg04(rs.getBigDecimal("FCST_QG04"))
                .fcstQg05(rs.getBigDecimal("FCST_QG05"))
                .fcstQg06(rs.getBigDecimal("FCST_QG06"))
                .fcstQgen(rs.getBigDecimal("FCST_QGEN"))
                .fcstQgmx(rs.getBigDecimal("FCST_QGMX"))
                .fcstQgmn(rs.getBigDecimal("FCST_QGMN"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataReFcstLfdWa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataReFcstLfdWa record) {
        return record.getUpdDate();
    }
}
