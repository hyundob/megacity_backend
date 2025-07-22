package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_HG_MEAS_GEM_GENT_DA")
@IdClass(GemGentDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataHgMeasGemGentDa {
    @Id
    @Column(length = 12, nullable = false)
    private String TM;

    @Id
    @Column(length = 20, nullable = false)
    private String AREA_GRP_CD;

    @Id
    @Column(length = 20, nullable = false)
    private String AREA_GRP_ID;

    @Column(precision = 18, scale = 5)
    private BigDecimal HGEN_PROD;
    @Column(precision = 18, scale = 5)
    private BigDecimal HGEN_CAPA;

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
