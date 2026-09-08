package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataMsMeasMetAwsH;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasMetAwsHDto {

    private String stnId;
    private String tm;
    private BigDecimal lat;
    private BigDecimal lon;
    private BigDecimal ht;
    private BigDecimal wd;
    private BigDecimal ws;
    private BigDecimal ta;
    private BigDecimal hm;
    private BigDecimal pa;
    private BigDecimal ps;
    private BigDecimal rnYn;
    private BigDecimal rn1hr;
    private BigDecimal rnDay;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static MeasMetAwsHDto from(RepDataMsMeasMetAwsH e) {
        return MeasMetAwsHDto.builder()
                .stnId(e.getStnId())
                .tm(e.getTm())
                .lat(e.getLat())
                .lon(e.getLon())
                .ht(e.getHt())
                .wd(e.getWd())
                .ws(e.getWs())
                .ta(e.getTa())
                .hm(e.getHm())
                .pa(e.getPa())
                .ps(e.getPs())
                .rnYn(e.getRnYn())
                .rn1hr(e.getRn1hr())
                .rnDay(e.getRnDay())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
