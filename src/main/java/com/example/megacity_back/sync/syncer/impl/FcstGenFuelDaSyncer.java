package com.example.megacity_back.sync.syncer.impl;

import com.example.megacity_back.entity.RepDataReFcstGenFuelDa;
import com.example.megacity_back.repository.RepDataReFcstGenFuelDaRepository;
import com.example.megacity_back.sync.config.SyncerContext;
import com.example.megacity_back.sync.syncer.AbstractTableSyncer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FcstGenFuelDaSyncer extends AbstractTableSyncer<RepDataReFcstGenFuelDa> {

    private final RepDataReFcstGenFuelDaRepository repo;

    public FcstGenFuelDaSyncer(SyncerContext ctx, RepDataReFcstGenFuelDaRepository repo) {
        super(ctx);
        this.repo = repo;
    }

    @Override
    public String tableName() {
        return "REP_DATA_RE_FCST_GEN_FUEL_DA";
    }

    @Override
    protected RowMapper<RepDataReFcstGenFuelDa> rowMapper() {
        return (rs, rowNum) -> RepDataReFcstGenFuelDa.builder()
                .pwrExcTpCd(rs.getString("PWR_EXC_TP_CD"))
                .fuelTpCd(rs.getString("FUEL_TP_CD"))
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
                .fcstQg07(rs.getBigDecimal("FCST_QG07"))
                .fcstQg08(rs.getBigDecimal("FCST_QG08"))
                .fcstQg11(rs.getBigDecimal("FCST_QG11"))
                .fcstQg12(rs.getBigDecimal("FCST_QG12"))
                .fcstQg21(rs.getBigDecimal("FCST_QG21"))
                .fcstQg22(rs.getBigDecimal("FCST_QG22"))
                .fcstQg23(rs.getBigDecimal("FCST_QG23"))
                .fcstQg31(rs.getBigDecimal("FCST_QG31"))
                .fcstQgen(rs.getBigDecimal("FCST_QGEN"))
                .fcstQgmx(rs.getBigDecimal("FCST_QGMX"))
                .fcstQgmn(rs.getBigDecimal("FCST_QGMN"))
                .fcstCapa(rs.getBigDecimal("FCST_CAPA"))
                .essCg01(rs.getBigDecimal("ESS_CG01"))
                .essCg02(rs.getBigDecimal("ESS_CG02"))
                .essCg03(rs.getBigDecimal("ESS_CG03"))
                .essCg04(rs.getBigDecimal("ESS_CG04"))
                .essCg05(rs.getBigDecimal("ESS_CG05"))
                .essCg06(rs.getBigDecimal("ESS_CG06"))
                .essCg07(rs.getBigDecimal("ESS_CG07"))
                .essCg08(rs.getBigDecimal("ESS_CG08"))
                .essCg11(rs.getBigDecimal("ESS_CG11"))
                .essCg12(rs.getBigDecimal("ESS_CG12"))
                .essCg21(rs.getBigDecimal("ESS_CG21"))
                .essCg22(rs.getBigDecimal("ESS_CG22"))
                .essCg23(rs.getBigDecimal("ESS_CG23"))
                .essCg31(rs.getBigDecimal("ESS_CG31"))
                .essPump(rs.getBigDecimal("ESS_PUMP"))
                .essQgen(rs.getBigDecimal("ESS_QGEN"))
                .essCapa(rs.getBigDecimal("ESS_CAPA"))
                .etcVari(rs.getString("ETC_VARI"))
                .regDate(rs.getObject("REG_DATE", LocalDateTime.class))
                .updDate(rs.getObject("UPD_DATE", LocalDateTime.class))
                .build();
    }

    @Override
    protected void bulkUpsert(@NonNull List<RepDataReFcstGenFuelDa> records) {
        repo.saveAll(records);
    }

    @Override
    protected LocalDateTime extractUpdDate(RepDataReFcstGenFuelDa record) {
        return record.getUpdDate();
    }
}
