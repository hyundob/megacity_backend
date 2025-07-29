package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_KPX_JEJU_SUKUB_M")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReKpxJejuSukubM {

    @Id
    @Column(name = "TM", length = 12, nullable = false)
    private String tm;

    @Column(name = "SUPP_ABILITY", precision = 18, scale = 5)
    private BigDecimal suppAbility;

    @Column(name = "CURR_PWR_TOT", precision = 18, scale = 5)
    private BigDecimal currPwrTot;

    @Column(name = "RENEW_PWR_TOT", precision = 18, scale = 5)
    private BigDecimal renewPwrTot;

    @Column(name = "RENEW_PWR_SOLAR", precision = 18, scale = 5)
    private BigDecimal renewPwrSolar;

    @Column(name = "RENEW_PWR_WIND", precision = 18, scale = 5)
    private BigDecimal renewPwrWind;

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
