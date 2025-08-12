package com.example.megacity_back.dto;

import lombok.*;

@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FcstLfdDaDto {
    private String crtnTm;
    private String fcstTm;
    private double fcstQgen;
    private double fcstQgmx;
    private double fcstQgmn;
}
