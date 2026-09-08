package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataReKpxSmpAreaH;
import com.example.megacity_back.repository.RepDataReKpxSmpAreaHRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class KpxSmpAreaHSyncer extends AbstractTableSyncer<RepDataReKpxSmpAreaH> {

    private final RepDataReKpxSmpAreaHRepository repo;

    public KpxSmpAreaHSyncer(SyncerContext ctx, RepDataReKpxSmpAreaHRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_RE_KPX_SMP_AREA_H";
    }

    @Override
    protected RowMapper<RepDataReKpxSmpAreaH> rowMapper() {
        return (rs, rowNum) -> RepDataReKpxSmpAreaH.builder()
                .tradeDay(rs.getString("TRADE_DAY"))
                .tradeHour(rs.getBigDecimal("TRADE_HOUR"))
                .areaCd(rs.getString("AREA_CD"))
                .smp(rs.getBigDecimal("SMP"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataReKpxSmpAreaH> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataReKpxSmpAreaH record) {
        return record.getUpdDate();
    }
}
