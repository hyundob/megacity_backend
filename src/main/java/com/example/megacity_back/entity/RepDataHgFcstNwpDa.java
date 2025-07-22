package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_HG_FCST_NWP_DA")
@IdClass(FcstNwpDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataHgFcstNwpDa {
    @Id
    @Column(length = 2, nullable = false)
    private String PWR_EXC_TP_CD;

    @Id
    @Column(length = 20, nullable = false)
    private String AREA_GRP_CD;

    @Id
    @Column(length = 20, nullable = false)
    private String AREA_GRP_ID;

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

    @Column(precision = 7, scale = 6)
    private BigDecimal FCST_SRAD;
    @Column(precision = 7, scale = 6)
    private BigDecimal FCST_TEMP;
    @Column(precision = 7, scale = 6)
    private BigDecimal FCST_HUMI;
    @Column(precision = 7, scale = 6)
    private BigDecimal FCST_WSPD;
    @Column(precision = 7, scale = 6)
    private BigDecimal FCST_PSFC;

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
