package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_HG_FCST_GEN_GENT_DA")
@IdClass(FcstGenGentDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RepDataHgFcstGenGentDa {
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

    @Column(length = 5, nullable = false)
    private String LEAD_TM;

    @Column(length = 2, nullable = false)
    private String FCST_PROD_CD;

    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_QGEN;
    @Column(precision = 13, scale = 6)
    private BigDecimal FCST_CAPA;

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
