package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_FCST_LFD_DA")
@IdClass(FcstLfdDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReFcstLfdDa {

    @Id
    @Column(length = 12, nullable = false)
    private String CRTN_TM;

    @Id
    @Column(length = 12, nullable = false)
    private String FCST_TM;

    @Column(length = 15)
    private String LEAD_TM;

    @Column(length = 2)
    private String FCST_PROD_CD;

    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG01;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG02;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG03;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG04;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG05;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QG06;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QGEN;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QGMX;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QGMN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime REG_DATE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime UPD_DATE;

    @PrePersist
    protected void onCreate() {
        this.REG_DATE = LocalDateTime.now();
        this.UPD_DATE = LocalDateTime.now();
    }
}
