package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataReFcstLfdWa;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcstLfdWaDto {

    private String crtnTm;
    private String fcstTm;
    private String leadTm;
    private String fcstProdCd;
    private BigDecimal fcstQg01;
    private BigDecimal fcstQg02;
    private BigDecimal fcstQg03;
    private BigDecimal fcstQg04;
    private BigDecimal fcstQg05;
    private BigDecimal fcstQg06;
    private BigDecimal fcstQgen;
    private BigDecimal fcstQgmx;
    private BigDecimal fcstQgmn;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static FcstLfdWaDto from(RepDataReFcstLfdWa e) {
        return FcstLfdWaDto.builder()
                .crtnTm(e.getCrtnTm())
                .fcstTm(e.getFcstTm())
                .leadTm(e.getLeadTm())
                .fcstProdCd(e.getFcstProdCd())
                .fcstQg01(e.getFcstQg01())
                .fcstQg02(e.getFcstQg02())
                .fcstQg03(e.getFcstQg03())
                .fcstQg04(e.getFcstQg04())
                .fcstQg05(e.getFcstQg05())
                .fcstQg06(e.getFcstQg06())
                .fcstQgen(e.getFcstQgen())
                .fcstQgmx(e.getFcstQgmx())
                .fcstQgmn(e.getFcstQgmn())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
