package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_MS_MEAS_MET_SHKO60_H")
@IdClass(MeasMetShko60HId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataMsMeasMetShko60H {

    @Id
    @Column(name = "STN_ID", length = 20, nullable = false)
    private String stnId;

    @Id
    @Column(name = "TM", length = 12, nullable = false)
    private String tm;

    @Column(name = "WD_DEG", precision = 6, scale = 0)
    private BigDecimal wdDeg;

    @Column(name = "WD", precision = 7, scale = 1)
    private BigDecimal wd;

    @Column(name = "WS", precision = 7, scale = 1)
    private BigDecimal ws;

    @Column(name = "WS_UNT", precision = 6, scale = 0)
    private BigDecimal wsUnt;

    @Column(name = "WD_GST", precision = 6, scale = 0)
    private BigDecimal wdGst;

    @Column(name = "WS_GST", precision = 7, scale = 1)
    private BigDecimal wsGst;

    @Column(name = "WS_GST_TM", precision = 6, scale = 0)
    private BigDecimal wsGstTm;

    @Column(name = "PA", precision = 7, scale = 1)
    private BigDecimal pa;

    @Column(name = "PS", precision = 7, scale = 1)
    private BigDecimal ps;

    @Column(name = "PR", precision = 7, scale = 1)
    private BigDecimal pr;

    @Column(name = "PT", precision = 6, scale = 0)
    private BigDecimal pt;

    @Column(name = "TA", precision = 7, scale = 1)
    private BigDecimal ta;

    @Column(name = "TD", precision = 7, scale = 1)
    private BigDecimal td;

    @Column(name = "HM", precision = 7, scale = 1)
    private BigDecimal hm;

    @Column(name = "PV", precision = 7, scale = 1)
    private BigDecimal pv;

    @Column(name = "RN", precision = 7, scale = 1)
    private BigDecimal rn;

    @Column(name = "RN_ACC", precision = 6, scale = 0)
    private BigDecimal rnAcc;

    @Column(name = "SD_HR1", precision = 7, scale = 1)
    private BigDecimal sdHr1;

    @Column(name = "SD_HR3", precision = 7, scale = 1)
    private BigDecimal sdHr3;

    @Column(name = "SD_DAY", precision = 7, scale = 1)
    private BigDecimal sdDay;

    @Column(name = "SD_TOT", precision = 7, scale = 1)
    private BigDecimal sdTot;

    @Column(name = "WC", precision = 6, scale = 0)
    private BigDecimal wc;

    @Column(name = "WP", precision = 6, scale = 0)
    private BigDecimal wp;

    @Column(name = "WW", precision = 6, scale = 0)
    private BigDecimal ww;

    @Column(name = "CA_TOT", precision = 6, scale = 0)
    private BigDecimal caTot;

    @Column(name = "CA_MID", precision = 6, scale = 0)
    private BigDecimal caMid;

    @Column(name = "CH_MIN", precision = 6, scale = 0)
    private BigDecimal chMin;

    @Column(name = "CT", precision = 6, scale = 0)
    private BigDecimal ct;

    @Column(name = "CT_TOP", precision = 6, scale = 0)
    private BigDecimal ctTop;

    @Column(name = "CT_MID", precision = 6, scale = 0)
    private BigDecimal ctMid;

    @Column(name = "CT_LOW", precision = 6, scale = 0)
    private BigDecimal ctLow;

    @Column(name = "VS", precision = 10, scale = 2)
    private BigDecimal vs;

    @Column(name = "SS_HR1", precision = 7, scale = 1)
    private BigDecimal ssHr1;

    @Column(name = "SI_HR1", precision = 7, scale = 2)
    private BigDecimal siHr1;

    @Column(name = "TG", precision = 7, scale = 1)
    private BigDecimal tg;

    @Column(name = "ST_GD", precision = 6, scale = 0)
    private BigDecimal stGd;

    @Column(name = "TS", precision = 7, scale = 1)
    private BigDecimal ts;

    @Column(name = "TE_005", precision = 7, scale = 1)
    private BigDecimal te005;

    @Column(name = "TE_01", precision = 7, scale = 1)
    private BigDecimal te01;

    @Column(name = "TE_02", precision = 7, scale = 1)
    private BigDecimal te02;

    @Column(name = "TE_03", precision = 7, scale = 1)
    private BigDecimal te03;

    @Column(name = "TE_05", precision = 7, scale = 1)
    private BigDecimal te05;

    @Column(name = "TE_10", precision = 7, scale = 1)
    private BigDecimal te10;

    @Column(name = "TE_15", precision = 7, scale = 1)
    private BigDecimal te15;

    @Column(name = "TE_30", precision = 7, scale = 1)
    private BigDecimal te30;

    @Column(name = "TE_50", precision = 7, scale = 1)
    private BigDecimal te50;

    @Column(name = "WH", precision = 7, scale = 1)
    private BigDecimal wh;

    @Column(name = "RN_INT", precision = 7, scale = 1)
    private BigDecimal rnInt;

    @Column(name = "RN_DAY", precision = 7, scale = 1)
    private BigDecimal rnDay;

    @Column(name = "ST_SEA", precision = 6, scale = 0)
    private BigDecimal stSea;

    @Column(name = "BF", precision = 6, scale = 0)
    private BigDecimal bf;

    @Column(name = "RN_JUN", precision = 6, scale = 0)
    private BigDecimal rnJun;

    @Column(name = "IR", precision = 6, scale = 0)
    private BigDecimal ir;

    @Column(name = "IX", precision = 6, scale = 0)
    private BigDecimal ix;

    @Column(name = "REG_DATE", nullable = false, updatable = false)
    private LocalDateTime regDate;

    @Column(name = "UPD_DATE", nullable = false)
    private LocalDateTime updDate;
    @PrePersist
    protected void onCreate() {
        if (this.regDate == null) this.regDate = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
        if (this.updDate == null) this.updDate = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
    }
}
