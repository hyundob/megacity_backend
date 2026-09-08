package com.example.megacity_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REP_DATA_MS_FCST_DFS_SHRT_H")
@IdClass(FcstDfsShrtHId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepDataMsFcstDfsShrtH {

    @Id
    @Column(name = "STN_ID", length = 20, nullable = false)
    private String stnId;

    @Id
    @Column(name = "CRTN_TM", length = 12, nullable = false)
    private String crtnTm;

    @Id
    @Column(name = "FCST_TM", length = 12, nullable = false)
    private String fcstTm;

    @Column(name = "LEAD_TM", length = 5)
    private String leadTm;

    @Column(name = "PCP", precision = 7, scale = 2)
    private BigDecimal pcp;

    @Column(name = "POP", precision = 7, scale = 2)
    private BigDecimal pop;

    @Column(name = "PTY", precision = 7, scale = 2)
    private BigDecimal pty;

    @Column(name = "REH", precision = 7, scale = 2)
    private BigDecimal reh;

    @Column(name = "SNO", precision = 7, scale = 2)
    private BigDecimal sno;

    @Column(name = "TMN", precision = 7, scale = 2)
    private BigDecimal tmn;

    @Column(name = "TMP", precision = 7, scale = 2)
    private BigDecimal tmp;

    @Column(name = "TMX", precision = 7, scale = 2)
    private BigDecimal tmx;

    @Column(name = "VEC", precision = 7, scale = 2)
    private BigDecimal vec;

    @Column(name = "WAV", precision = 7, scale = 2)
    private BigDecimal wav;

    @Column(name = "WSD", precision = 7, scale = 2)
    private BigDecimal wsd;

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
