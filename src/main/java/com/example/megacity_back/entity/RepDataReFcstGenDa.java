package com.example.megacity_back.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_FCST_GEN_DA")
@IdClass(FcstGenDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReFcstGenDa {
    @Id
    @Column(length = 2, nullable = false)
    private String PWR_EXC_TP_CD;

    @Id
    @Column(length = 20, nullable = false)
    private String FUEL_TP_CD;

    @Id
    @Column(length = 12, nullable = false)
    private String CRTN_TM;

    @Id
    @Column(length = 12, nullable = false)
    private String FCST_TM;

    @Column(length = 5)
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
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_CAPA;
    @Column(precision = 13, scale = 6)
    private BigDecimal ESS_CHRG;
    @Column(precision = 13, scale = 6)
    private BigDecimal ESS_DISC;
    @Column(precision = 13, scale = 6)
    private BigDecimal ESS_CAPA;

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
