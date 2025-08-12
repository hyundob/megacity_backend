package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_FCST_LFD_DA")
@IdClass(FcstLfdDaId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReFcstLfdDa {

    @Id
    @Column(name = "CRTN_TM", length = 12, nullable = false)
    private String crtnTm;

    @Id
    @Column(name = "FCST_TM", length = 12, nullable = false)
    private String fcstTm;

    @Column(name = "LEAD_TM", length = 15)
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

    @Column(name = "FCST_QGEN", precision = 13, scale = 6)
    private BigDecimal fcstQgen;

    @Column(name = "FCST_QGMX", precision = 13, scale = 6)
    private BigDecimal fcstQgmx;

    @Column(name = "FCST_QGMN", precision = 13, scale = 6)
    private BigDecimal fcstQgmn;

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

