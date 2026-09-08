package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataReKpxLfdAreaH;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpxLfdAreaHDto {

    private String tradeDay;
    private String areaCd;
    private BigDecimal hh01;
    private BigDecimal hh02;
    private BigDecimal hh03;
    private BigDecimal hh04;
    private BigDecimal hh05;
    private BigDecimal hh06;
    private BigDecimal hh07;
    private BigDecimal hh08;
    private BigDecimal hh09;
    private BigDecimal hh10;
    private BigDecimal hh11;
    private BigDecimal hh12;
    private BigDecimal hh13;
    private BigDecimal hh14;
    private BigDecimal hh15;
    private BigDecimal hh16;
    private BigDecimal hh17;
    private BigDecimal hh18;
    private BigDecimal hh19;
    private BigDecimal hh20;
    private BigDecimal hh21;
    private BigDecimal hh22;
    private BigDecimal hh23;
    private BigDecimal hh24;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static KpxLfdAreaHDto from(RepDataReKpxLfdAreaH e) {
        return KpxLfdAreaHDto.builder()
                .tradeDay(e.getTradeDay())
                .areaCd(e.getAreaCd())
                .hh01(e.getHh01())
                .hh02(e.getHh02())
                .hh03(e.getHh03())
                .hh04(e.getHh04())
                .hh05(e.getHh05())
                .hh06(e.getHh06())
                .hh07(e.getHh07())
                .hh08(e.getHh08())
                .hh09(e.getHh09())
                .hh10(e.getHh10())
                .hh11(e.getHh11())
                .hh12(e.getHh12())
                .hh13(e.getHh13())
                .hh14(e.getHh14())
                .hh15(e.getHh15())
                .hh16(e.getHh16())
                .hh17(e.getHh17())
                .hh18(e.getHh18())
                .hh19(e.getHh19())
                .hh20(e.getHh20())
                .hh21(e.getHh21())
                .hh22(e.getHh22())
                .hh23(e.getHh23())
                .hh24(e.getHh24())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
