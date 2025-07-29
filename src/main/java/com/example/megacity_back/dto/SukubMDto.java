package com.example.megacity_back.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor
@NoArgsConstructor @Builder
public class SukubMDto {
    private String tm;
    private BigDecimal suppAbility;
    private BigDecimal currPwrTot;
    private BigDecimal renewPwrTot;
    private BigDecimal renewPwrSolar;
    private BigDecimal renewPwrWind;
}
