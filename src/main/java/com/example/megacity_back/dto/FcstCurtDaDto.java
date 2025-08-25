package com.example.megacity_back.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder

public class FcstCurtDaDto {
    private String fcstTm;
    private double fcstMinpw;
    private double fcstCurt;
}
