package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_MS_MEAS_MET_AWS_H")
@IdClass(MeasMetAwsHId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataMsMeasMetAwsH {

    @Id
    @Column(name = "STN_ID", length = 20, nullable = false)
    private String stnId;

    @Id
    @Column(name = "TM", length = 12, nullable = false)
    private String tm;

    @Column(name = "LAT", precision = 10, scale = 6)
    private BigDecimal lat;

    @Column(name = "LON", precision = 10, scale = 6)
    private BigDecimal lon;

    @Column(name = "HT", precision = 7, scale = 2)
    private BigDecimal ht;

    @Column(name = "WD", precision = 7, scale = 1)
    private BigDecimal wd;

    @Column(name = "WS", precision = 7, scale = 1)
    private BigDecimal ws;

    @Column(name = "TA", precision = 7, scale = 1)
    private BigDecimal ta;

    @Column(name = "HM", precision = 7, scale = 1)
    private BigDecimal hm;

    @Column(name = "PA", precision = 7, scale = 1)
    private BigDecimal pa;

    @Column(name = "PS", precision = 7, scale = 1)
    private BigDecimal ps;

    @Column(name = "RN_YN", precision = 7, scale = 0)
    private BigDecimal rnYn;

    @Column(name = "RN_1HR", precision = 7, scale = 1)
    private BigDecimal rn1hr;

    @Column(name = "RN_DAY", precision = 7, scale = 1)
    private BigDecimal rnDay;

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
