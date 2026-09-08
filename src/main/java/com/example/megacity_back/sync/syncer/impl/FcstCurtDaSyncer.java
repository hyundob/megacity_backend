package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataHgFcstCurtDa;
import com.example.megacity_back.repository.RepDataHgFcstCurtDaRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FcstCurtDaSyncer extends AbstractTableSyncer<RepDataHgFcstCurtDa> {

    private final RepDataHgFcstCurtDaRepository repo;

    public FcstCurtDaSyncer(SyncerContext ctx, RepDataHgFcstCurtDaRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_HG_FCST_CURT_DA";
    }

    @Override
    protected RowMapper<RepDataHgFcstCurtDa> rowMapper() {
        return (rs, rowNum) -> RepDataHgFcstCurtDa.builder()
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
    protected void bulkUpsert(@NonNull List<RepDataHgFcstCurtDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataHgFcstCurtDa record) {
        return record.getUpdDate();
    }
}
