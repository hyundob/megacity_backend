package com.example.megacity_back.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "REP_DATA_RE_FCST_GEN_DA")
@IdClass(FcstGenDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataReFcstGenDa {
    @Id
    @Column(length = 2, nullable = false)
    private String PWR_EXC_TP_CD;

    @Id
    @Column(length = 20, nullable = false)
    private String FUEL_TP_CD;

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
}
