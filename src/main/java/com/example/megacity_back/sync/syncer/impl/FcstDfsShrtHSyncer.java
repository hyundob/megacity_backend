package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataMsFcstDfsShrtH;
import com.example.megacity_back.repository.RepDataMsFcstDfsShrtHRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FcstDfsShrtHSyncer extends AbstractTableSyncer<RepDataMsFcstDfsShrtH> {

    private final RepDataMsFcstDfsShrtHRepository repo;

    public FcstDfsShrtHSyncer(SyncerContext ctx, RepDataMsFcstDfsShrtHRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_MS_FCST_DFS_SHRT_H";
    }

    @Override
    protected RowMapper<RepDataMsFcstDfsShrtH> rowMapper() {
        return (rs, rowNum) -> RepDataMsFcstDfsShrtH.builder()
                .stnId(rs.getString("STN_ID"))
                .crtnTm(rs.getString("CRTN_TM"))
                .fcstTm(rs.getString("FCST_TM"))
                .leadTm(rs.getString("LEAD_TM"))
                .pcp(rs.getBigDecimal("PCP"))
                .pop(rs.getBigDecimal("POP"))
                .pty(rs.getBigDecimal("PTY"))
                .reh(rs.getBigDecimal("REH"))
                .sno(rs.getBigDecimal("SNO"))
                .tmn(rs.getBigDecimal("TMN"))
                .tmp(rs.getBigDecimal("TMP"))
                .tmx(rs.getBigDecimal("TMX"))
                .vec(rs.getBigDecimal("VEC"))
                .wav(rs.getBigDecimal("WAV"))
                .wsd(rs.getBigDecimal("WSD"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataMsFcstDfsShrtH> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataMsFcstDfsShrtH record) {
        return record.getUpdDate();
    }
}
