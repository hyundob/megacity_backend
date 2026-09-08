package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_KPX_LFD_AREA_H")
@IdClass(KpxLfdAreaHId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReKpxLfdAreaH {

    @Id
    @Column(name = "TRADE_DAY", length = 8, nullable = false)
    private String tradeDay;

    @Id
    @Column(name = "AREA_CD", length = 2, nullable = false)
    private String areaCd;

    @Column(name = "HH_01", precision = 10, scale = 2)
    private BigDecimal hh01;

    @Column(name = "HH_02", precision = 10, scale = 2)
    private BigDecimal hh02;

    @Column(name = "HH_03", precision = 10, scale = 2)
    private BigDecimal hh03;

    @Column(name = "HH_04", precision = 10, scale = 2)
    private BigDecimal hh04;

    @Column(name = "HH_05", precision = 10, scale = 2)
    private BigDecimal hh05;

    @Column(name = "HH_06", precision = 10, scale = 2)
    private BigDecimal hh06;

    @Column(name = "HH_07", precision = 10, scale = 2)
    private BigDecimal hh07;

    @Column(name = "HH_08", precision = 10, scale = 2)
    private BigDecimal hh08;

    @Column(name = "HH_09", precision = 10, scale = 2)
    private BigDecimal hh09;

    @Column(name = "HH_10", precision = 10, scale = 2)
    private BigDecimal hh10;

    @Column(name = "HH_11", precision = 10, scale = 2)
    private BigDecimal hh11;

    @Column(name = "HH_12", precision = 10, scale = 2)
    private BigDecimal hh12;

    @Column(name = "HH_13", precision = 10, scale = 2)
    private BigDecimal hh13;

    @Column(name = "HH_14", precision = 10, scale = 2)
    private BigDecimal hh14;

    @Column(name = "HH_15", precision = 10, scale = 2)
    private BigDecimal hh15;

    @Column(name = "HH_16", precision = 10, scale = 2)
    private BigDecimal hh16;

    @Column(name = "HH_17", precision = 10, scale = 2)
    private BigDecimal hh17;

    @Column(name = "HH_18", precision = 10, scale = 2)
    private BigDecimal hh18;

    @Column(name = "HH_19", precision = 10, scale = 2)
    private BigDecimal hh19;

    @Column(name = "HH_20", precision = 10, scale = 2)
    private BigDecimal hh20;

    @Column(name = "HH_21", precision = 10, scale = 2)
    private BigDecimal hh21;

    @Column(name = "HH_22", precision = 10, scale = 2)
    private BigDecimal hh22;

    @Column(name = "HH_23", precision = 10, scale = 2)
    private BigDecimal hh23;

    @Column(name = "HH_24", precision = 10, scale = 2)
    private BigDecimal hh24;

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
