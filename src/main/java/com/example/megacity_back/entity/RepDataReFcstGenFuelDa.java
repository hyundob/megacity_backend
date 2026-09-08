package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_FCST_GEN_FUEL_DA")
@IdClass(FcstGenFuelDaId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReFcstGenFuelDa {

    @Id
    @Column(name = "PWR_EXC_TP_CD", length = 2, nullable = false)
    private String pwrExcTpCd;

    @Id
    @Column(name = "FUEL_TP_CD", length = 20, nullable = false)
    private String fuelTpCd;

    @Id
    @Column(name = "CRTN_TM", length = 12, nullable = false)
    private String crtnTm;

    @Id
    @Column(name = "FCST_TM", length = 12, nullable = false)
    private String fcstTm;

    @Column(name = "LEAD_TM", length = 5)
    private String leadTm;

    @Column(name = "FCST_PROD_CD", length = 2)
    private String fcstProdCd;

    @Column(name = "FCST_QG01", precision = 13, scale = 6)
    private BigDecimal fcstQg01;

    @Column(name = "FCST_QG02", precision = 13, scale = 6)
    private BigDecimal fcstQg02;

    @Column(name = "FCST_QG03", precision = 13, scale = 6)
    private BigDecimal fcstQg03;

    @Column(name = "FCST_QG04", precision = 13, scale = 6)
    private BigDecimal fcstQg04;

    @Column(name = "FCST_QG05", precision = 13, scale = 6)
    private BigDecimal fcstQg05;

    @Column(name = "FCST_QG06", precision = 13, scale = 6)
    private BigDecimal fcstQg06;

    @Column(name = "FCST_QG07", precision = 13, scale = 6)
    private BigDecimal fcstQg07;

    @Column(name = "FCST_QG08", precision = 13, scale = 6)
    private BigDecimal fcstQg08;

    @Column(name = "FCST_QG11", precision = 13, scale = 6)
    private BigDecimal fcstQg11;

    @Column(name = "FCST_QG12", precision = 13, scale = 6)
    private BigDecimal fcstQg12;

    @Column(name = "FCST_QG21", precision = 13, scale = 6)
    private BigDecimal fcstQg21;

    @Column(name = "FCST_QG22", precision = 13, scale = 6)
    private BigDecimal fcstQg22;

    @Column(name = "FCST_QG23", precision = 13, scale = 6)
    private BigDecimal fcstQg23;

    @Column(name = "FCST_QG31", precision = 13, scale = 6)
    private BigDecimal fcstQg31;

    @Column(name = "FCST_QGEN", precision = 13, scale = 6)
    private BigDecimal fcstQgen;

    @Column(name = "FCST_QGMX", precision = 13, scale = 6)
    private BigDecimal fcstQgmx;

    @Column(name = "FCST_QGMN", precision = 13, scale = 6)
    private BigDecimal fcstQgmn;

    @Column(name = "FCST_CAPA", precision = 13, scale = 6)
    private BigDecimal fcstCapa;

    @Column(name = "ESS_CG01", precision = 13, scale = 6)
    private BigDecimal essCg01;

    @Column(name = "ESS_CG02", precision = 13, scale = 6)
    private BigDecimal essCg02;

    @Column(name = "ESS_CG03", precision = 13, scale = 6)
    private BigDecimal essCg03;

    @Column(name = "ESS_CG04", precision = 13, scale = 6)
    private BigDecimal essCg04;

    @Column(name = "ESS_CG05", precision = 13, scale = 6)
    private BigDecimal essCg05;

    @Column(name = "ESS_CG06", precision = 13, scale = 6)
    private BigDecimal essCg06;

    @Column(name = "ESS_CG07", precision = 13, scale = 6)
    private BigDecimal essCg07;

    @Column(name = "ESS_CG08", precision = 13, scale = 6)
    private BigDecimal essCg08;

    @Column(name = "ESS_CG11", precision = 13, scale = 6)
    private BigDecimal essCg11;

    @Column(name = "ESS_CG12", precision = 13, scale = 6)
    private BigDecimal essCg12;

    @Column(name = "ESS_CG21", precision = 13, scale = 6)
    private BigDecimal essCg21;

    @Column(name = "ESS_CG22", precision = 13, scale = 6)
    private BigDecimal essCg22;

    @Column(name = "ESS_CG23", precision = 13, scale = 6)
    private BigDecimal essCg23;

    @Column(name = "ESS_CG31", precision = 13, scale = 6)
    private BigDecimal essCg31;

    @Column(name = "ESS_PUMP", precision = 13, scale = 6)
    private BigDecimal essPump;

    @Column(name = "ESS_QGEN", precision = 13, scale = 6)
    private BigDecimal essQgen;

    @Column(name = "ESS_CAPA", precision = 13, scale = 6)
    private BigDecimal essCapa;

    @Column(name = "ETC_VARI", length = 600)
    private String etcVari;

    @Column(name = "REG_DATE", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @Column(name = "UPD_DATE", nullable = false, updatable = false)
    private LocalDateTime updDate;
    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }
}
