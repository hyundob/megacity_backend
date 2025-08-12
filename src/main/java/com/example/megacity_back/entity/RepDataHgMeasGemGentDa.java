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
    @Column(name = "TM", length = 12, nullable = false)
    private String tm;

    @Id
    @Column(name = "AREA_GRP_CD", length = 20, nullable = false)
    private String areaGrpCd;

    @Id
    @Column(name = "AREA_GRP_ID", length = 20, nullable = false)
    private String areaGrpId;

    @Column(name = "HGEN_PROD", precision = 18, scale = 5)
    private BigDecimal hgenProd;

    @Column(name = "HGEN_CAPA", precision = 18, scale = 5)
    private BigDecimal hgenCapa;

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
