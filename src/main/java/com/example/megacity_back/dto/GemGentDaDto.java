package com.example.megacity_back.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GemGentDaDto {
    private String areaGrpCd;
    private String tm;
    private double hgenProd;
    private double hgenCapa;
}
