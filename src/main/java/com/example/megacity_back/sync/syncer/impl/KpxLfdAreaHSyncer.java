package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataReKpxLfdAreaH;
import com.example.megacity_back.repository.RepDataReKpxLfdAreaHRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class KpxLfdAreaHSyncer extends AbstractTableSyncer<RepDataReKpxLfdAreaH> {

    private final RepDataReKpxLfdAreaHRepository repo;

    public KpxLfdAreaHSyncer(SyncerContext ctx, RepDataReKpxLfdAreaHRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_RE_KPX_LFD_AREA_H";
    }

    @Override
    protected RowMapper<RepDataReKpxLfdAreaH> rowMapper() {
        return (rs, rowNum) -> RepDataReKpxLfdAreaH.builder()
                .tradeDay(rs.getString("TRADE_DAY"))
                .areaCd(rs.getString("AREA_CD"))
                .hh01(rs.getBigDecimal("HH_01"))
                .hh02(rs.getBigDecimal("HH_02"))
                .hh03(rs.getBigDecimal("HH_03"))
                .hh04(rs.getBigDecimal("HH_04"))
                .hh05(rs.getBigDecimal("HH_05"))
                .hh06(rs.getBigDecimal("HH_06"))
                .hh07(rs.getBigDecimal("HH_07"))
                .hh08(rs.getBigDecimal("HH_08"))
                .hh09(rs.getBigDecimal("HH_09"))
                .hh10(rs.getBigDecimal("HH_10"))
                .hh11(rs.getBigDecimal("HH_11"))
                .hh12(rs.getBigDecimal("HH_12"))
                .hh13(rs.getBigDecimal("HH_13"))
                .hh14(rs.getBigDecimal("HH_14"))
                .hh15(rs.getBigDecimal("HH_15"))
                .hh16(rs.getBigDecimal("HH_16"))
                .hh17(rs.getBigDecimal("HH_17"))
                .hh18(rs.getBigDecimal("HH_18"))
                .hh19(rs.getBigDecimal("HH_19"))
                .hh20(rs.getBigDecimal("HH_20"))
                .hh21(rs.getBigDecimal("HH_21"))
                .hh22(rs.getBigDecimal("HH_22"))
                .hh23(rs.getBigDecimal("HH_23"))
                .hh24(rs.getBigDecimal("HH_24"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataReKpxLfdAreaH> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataReKpxLfdAreaH record) {
        return record.getUpdDate();
    }
}
