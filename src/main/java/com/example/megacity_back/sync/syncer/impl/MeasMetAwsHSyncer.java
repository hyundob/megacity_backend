package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataMsMeasMetAwsH;
import com.example.megacity_back.repository.RepDataMsMeasMetAwsHRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MeasMetAwsHSyncer extends AbstractTableSyncer<RepDataMsMeasMetAwsH> {

    private final RepDataMsMeasMetAwsHRepository repo;

    public MeasMetAwsHSyncer(SyncerContext ctx, RepDataMsMeasMetAwsHRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_MS_MEAS_MET_AWS_H";
    }

    @Override
    protected RowMapper<RepDataMsMeasMetAwsH> rowMapper() {
        return (rs, rowNum) -> RepDataMsMeasMetAwsH.builder()
                .stnId(rs.getString("STN_ID"))
                .tm(rs.getString("TM"))
                .lat(rs.getBigDecimal("LAT"))
                .lon(rs.getBigDecimal("LON"))
                .ht(rs.getBigDecimal("HT"))
                .wd(rs.getBigDecimal("WD"))
                .ws(rs.getBigDecimal("WS"))
                .ta(rs.getBigDecimal("TA"))
                .hm(rs.getBigDecimal("HM"))
                .pa(rs.getBigDecimal("PA"))
                .ps(rs.getBigDecimal("PS"))
                .rnYn(rs.getBigDecimal("RN_YN"))
                .rn1hr(rs.getBigDecimal("RN_1HR"))
                .rnDay(rs.getBigDecimal("RN_DAY"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataMsMeasMetAwsH> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataMsMeasMetAwsH record) {
        return record.getUpdDate();
    }
}
