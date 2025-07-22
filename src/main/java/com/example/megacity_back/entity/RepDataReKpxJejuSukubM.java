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
    @Column(length = 12, nullable = false)
    private String TM;

    @Column(precision = 18, scale = 5)
    private BigDecimal SUPP_ABILITY;
    @Column(precision = 18, scale = 5)
    private BigDecimal CURR_PWR_TOT;
    @Column(precision = 18, scale = 5)
    private BigDecimal RENEW_PWR_TOT;
    @Column(precision = 18, scale = 5)
    private BigDecimal RENEW_PWR_SOLAR;
    @Column(precision = 18, scale = 5)
    private BigDecimal RENEW_PWR_WIND;

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
