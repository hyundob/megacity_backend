package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataReKpxSmpAreaH;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpxSmpAreaHDto {

    private String tradeDay;
    private BigDecimal tradeHour;
    private String areaCd;
    private BigDecimal smp;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static KpxSmpAreaHDto from(RepDataReKpxSmpAreaH e) {
        return KpxSmpAreaHDto.builder()
                .tradeDay(e.getTradeDay())
                .tradeHour(e.getTradeHour())
                .areaCd(e.getAreaCd())
                .smp(e.getSmp())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
