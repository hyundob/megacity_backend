package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataMsFcstDfsShrtH;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcstDfsShrtHDto {

    private String stnId;
    private String crtnTm;
    private String fcstTm;
    private String leadTm;
    private BigDecimal pcp;
    private BigDecimal pop;
    private BigDecimal pty;
    private BigDecimal reh;
    private BigDecimal sno;
    private BigDecimal tmn;
    private BigDecimal tmp;
    private BigDecimal tmx;
    private BigDecimal vec;
    private BigDecimal wav;
    private BigDecimal wsd;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static FcstDfsShrtHDto from(RepDataMsFcstDfsShrtH e) {
        return FcstDfsShrtHDto.builder()
                .stnId(e.getStnId())
                .crtnTm(e.getCrtnTm())
                .fcstTm(e.getFcstTm())
                .leadTm(e.getLeadTm())
                .pcp(e.getPcp())
                .pop(e.getPop())
                .pty(e.getPty())
                .reh(e.getReh())
                .sno(e.getSno())
                .tmn(e.getTmn())
                .tmp(e.getTmp())
                .tmx(e.getTmx())
                .vec(e.getVec())
                .wav(e.getWav())
                .wsd(e.getWsd())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
