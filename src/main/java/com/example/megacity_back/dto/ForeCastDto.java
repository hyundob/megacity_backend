package com.example.megacity_back.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor
@NoArgsConstructor @Builder
public class ForeCastDto {
    private String crtnTm;
    private String fcstTm;
    private String areaGrpId;
    private BigDecimal fcstSrad;
    private BigDecimal fcstTemp;
    private BigDecimal fcstHumi;
    private BigDecimal fcstWspd;
    private BigDecimal fcstPsfc;
}
