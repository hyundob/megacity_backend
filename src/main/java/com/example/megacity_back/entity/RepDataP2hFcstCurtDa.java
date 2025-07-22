package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_P2H_FCST_CURT_DA")
@IdClass(FcstCurtDaId.class)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataP2hFcstCurtDa {
    @Id
    @Column(length = 12, nullable = false)
    private String CRTN_TM;

    @Id
    @Column(length = 12, nullable = false)
    private String FCST_TM;

    @Column(length = 5)
    private String LEAD_TM;

    @Column(precision = 7, scale = 2)
    private BigDecimal FCST_MINPW;
    @Column(precision = 7, scale = 2)
    private BigDecimal FCST_CURT;

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
