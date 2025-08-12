package com.example.megacity_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FcstGenDaChartDto {
    private String fcstTm;     // 예측시간 (yyyyMMddHHmm)
    private double fcstQgen;       // 최종발전량
    private double fcstQgmx;       // 예측최대
    private double fcstQgmn;       // 예측최소
    private double fcstCapa;  // 예측설비용량(MW)
}