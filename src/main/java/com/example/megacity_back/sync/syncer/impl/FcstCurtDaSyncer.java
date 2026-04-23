package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataP2hFcstCurtDa;
import com.example.megacity_back.repository.RepDataP2hFcstCurtDaRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FcstCurtDaSyncer extends AbstractTableSyncer<RepDataP2hFcstCurtDa> {

    private final RepDataP2hFcstCurtDaRepository repo;

    public FcstCurtDaSyncer(SyncerContext ctx, RepDataP2hFcstCurtDaRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_P2H_FCST_CURT_DA";
    }

    @Override
    protected RowMapper<RepDataP2hFcstCurtDa> rowMapper() {
        return (rs, rowNum) -> RepDataP2hFcstCurtDa.builder()
                .crtnTm(rs.getString("CRTN_TM"))
                .fcstTm(rs.getString("FCST_TM"))
                .leadTm(rs.getString("LEAD_TM"))
                .fcstMinpw(rs.getBigDecimal("FCST_MINPW"))
                .fcstCurt(rs.getBigDecimal("FCST_CURT"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataP2hFcstCurtDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataP2hFcstCurtDa record) {
        return record.getUpdDate();
    }
}
