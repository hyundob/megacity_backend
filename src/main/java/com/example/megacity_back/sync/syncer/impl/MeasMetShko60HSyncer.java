package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataMsMeasMetShko60H;
import com.example.megacity_back.repository.RepDataMsMeasMetShko60HRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MeasMetShko60HSyncer extends AbstractTableSyncer<RepDataMsMeasMetShko60H> {

    private final RepDataMsMeasMetShko60HRepository repo;

    public MeasMetShko60HSyncer(SyncerContext ctx, RepDataMsMeasMetShko60HRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_MS_MEAS_MET_SHKO60_H";
    }

    @Override
    protected RowMapper<RepDataMsMeasMetShko60H> rowMapper() {
        return (rs, rowNum) -> RepDataMsMeasMetShko60H.builder()
                .stnId(rs.getString("STN_ID"))
                .tm(rs.getString("TM"))
                .wdDeg(rs.getBigDecimal("WD_DEG"))
                .wd(rs.getBigDecimal("WD"))
                .ws(rs.getBigDecimal("WS"))
                .wsUnt(rs.getBigDecimal("WS_UNT"))
                .wdGst(rs.getBigDecimal("WD_GST"))
                .wsGst(rs.getBigDecimal("WS_GST"))
                .wsGstTm(rs.getBigDecimal("WS_GST_TM"))
                .pa(rs.getBigDecimal("PA"))
                .ps(rs.getBigDecimal("PS"))
                .pr(rs.getBigDecimal("PR"))
                .pt(rs.getBigDecimal("PT"))
                .ta(rs.getBigDecimal("TA"))
                .td(rs.getBigDecimal("TD"))
                .hm(rs.getBigDecimal("HM"))
                .pv(rs.getBigDecimal("PV"))
                .rn(rs.getBigDecimal("RN"))
                .rnAcc(rs.getBigDecimal("RN_ACC"))
                .sdHr1(rs.getBigDecimal("SD_HR1"))
                .sdHr3(rs.getBigDecimal("SD_HR3"))
                .sdDay(rs.getBigDecimal("SD_DAY"))
                .sdTot(rs.getBigDecimal("SD_TOT"))
                .wc(rs.getBigDecimal("WC"))
                .wp(rs.getBigDecimal("WP"))
                .ww(rs.getBigDecimal("WW"))
                .caTot(rs.getBigDecimal("CA_TOT"))
                .caMid(rs.getBigDecimal("CA_MID"))
                .chMin(rs.getBigDecimal("CH_MIN"))
                .ct(rs.getBigDecimal("CT"))
                .ctTop(rs.getBigDecimal("CT_TOP"))
                .ctMid(rs.getBigDecimal("CT_MID"))
                .ctLow(rs.getBigDecimal("CT_LOW"))
                .vs(rs.getBigDecimal("VS"))
                .ssHr1(rs.getBigDecimal("SS_HR1"))
                .siHr1(rs.getBigDecimal("SI_HR1"))
                .tg(rs.getBigDecimal("TG"))
                .stGd(rs.getBigDecimal("ST_GD"))
                .ts(rs.getBigDecimal("TS"))
                .te005(rs.getBigDecimal("TE_005"))
                .te01(rs.getBigDecimal("TE_01"))
                .te02(rs.getBigDecimal("TE_02"))
                .te03(rs.getBigDecimal("TE_03"))
                .te05(rs.getBigDecimal("TE_05"))
                .te10(rs.getBigDecimal("TE_10"))
                .te15(rs.getBigDecimal("TE_15"))
                .te30(rs.getBigDecimal("TE_30"))
                .te50(rs.getBigDecimal("TE_50"))
                .wh(rs.getBigDecimal("WH"))
                .rnInt(rs.getBigDecimal("RN_INT"))
                .rnDay(rs.getBigDecimal("RN_DAY"))
                .stSea(rs.getBigDecimal("ST_SEA"))
                .bf(rs.getBigDecimal("BF"))
                .rnJun(rs.getBigDecimal("RN_JUN"))
                .ir(rs.getBigDecimal("IR"))
                .ix(rs.getBigDecimal("IX"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataMsMeasMetShko60H> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataMsMeasMetShko60H record) {
        return record.getUpdDate();
    }
}
