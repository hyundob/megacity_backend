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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataHgFcstGenGentDa {

    @Id
    @Column(name = "AREA_GRP_CD", length = 20, nullable = false)
    private String areaGrpCd;

    @Id
    @Column(name = "AREA_GRP_ID", length = 20, nullable = false)
    private String areaGrpId;

    @Id
    @Column(name = "CRTN_TM", length = 12, nullable = false)
    private String crtnTm;

    @Id
    @Column(name = "FCST_TM", length = 12, nullable = false)
    private String fcstTm;

    @Column(name = "LEAD_TM", length = 5, nullable = false)
    private String leadTm;

    @Column(name = "FCST_PROD_CD", length = 2, nullable = false)
    private String fcstProdCd;

    @Column(name = "FCST_QGEN", precision = 13, scale = 6)
    private BigDecimal fcstQgen;

    @Column(name = "FCST_CAPA", precision = 13, scale = 6)
    private BigDecimal fcstCapa;

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