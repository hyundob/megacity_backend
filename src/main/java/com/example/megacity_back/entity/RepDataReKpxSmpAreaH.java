package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_RE_KPX_SMP_AREA_H")
@IdClass(KpxSmpAreaHId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReKpxSmpAreaH {

    @Id
    @Column(name = "TRADE_DAY", length = 8, nullable = false)
    private String tradeDay;

    @Id
    @Column(name = "TRADE_HOUR", precision = 2, scale = 0, nullable = false)
    private BigDecimal tradeHour;

    @Id
    @Column(name = "AREA_CD", length = 2, nullable = false)
    private String areaCd;

    @Column(name = "SMP", precision = 10, scale = 2)
    private BigDecimal smp;

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
