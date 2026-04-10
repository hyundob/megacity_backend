package com.example.megacity_back.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenGentDaDto {
    private String areaGrpCd;
    private String areaGrpId;
    private String fcstTm;
    private double fcstQgen;
    private double fcstCapa;
}
