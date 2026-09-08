package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataReFcstGenFuelDa;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcstGenFuelDaDto {

    private String pwrExcTpCd;
    private String fuelTpCd;
    private String crtnTm;
    private String fcstTm;
    private String leadTm;
    private String fcstProdCd;
    private BigDecimal fcstQg01;
    private BigDecimal fcstQg02;
    private BigDecimal fcstQg03;
    private BigDecimal fcstQg04;
    private BigDecimal fcstQg05;
    private BigDecimal fcstQg06;
    private BigDecimal fcstQg07;
    private BigDecimal fcstQg08;
    private BigDecimal fcstQg11;
    private BigDecimal fcstQg12;
    private BigDecimal fcstQg21;
    private BigDecimal fcstQg22;
    private BigDecimal fcstQg23;
    private BigDecimal fcstQg31;
    private BigDecimal fcstQgen;
    private BigDecimal fcstQgmx;
    private BigDecimal fcstQgmn;
    private BigDecimal fcstCapa;
    private BigDecimal essCg01;
    private BigDecimal essCg02;
    private BigDecimal essCg03;
    private BigDecimal essCg04;
    private BigDecimal essCg05;
    private BigDecimal essCg06;
    private BigDecimal essCg07;
    private BigDecimal essCg08;
    private BigDecimal essCg11;
    private BigDecimal essCg12;
    private BigDecimal essCg21;
    private BigDecimal essCg22;
    private BigDecimal essCg23;
    private BigDecimal essCg31;
    private BigDecimal essPump;
    private BigDecimal essQgen;
    private BigDecimal essCapa;
    private String etcVari;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static FcstGenFuelDaDto from(RepDataReFcstGenFuelDa e) {
        return FcstGenFuelDaDto.builder()
                .pwrExcTpCd(e.getPwrExcTpCd())
                .fuelTpCd(e.getFuelTpCd())
                .crtnTm(e.getCrtnTm())
                .fcstTm(e.getFcstTm())
                .leadTm(e.getLeadTm())
                .fcstProdCd(e.getFcstProdCd())
                .fcstQg01(e.getFcstQg01())
                .fcstQg02(e.getFcstQg02())
                .fcstQg03(e.getFcstQg03())
                .fcstQg04(e.getFcstQg04())
                .fcstQg05(e.getFcstQg05())
                .fcstQg06(e.getFcstQg06())
                .fcstQg07(e.getFcstQg07())
                .fcstQg08(e.getFcstQg08())
                .fcstQg11(e.getFcstQg11())
                .fcstQg12(e.getFcstQg12())
                .fcstQg21(e.getFcstQg21())
                .fcstQg22(e.getFcstQg22())
                .fcstQg23(e.getFcstQg23())
                .fcstQg31(e.getFcstQg31())
                .fcstQgen(e.getFcstQgen())
                .fcstQgmx(e.getFcstQgmx())
                .fcstQgmn(e.getFcstQgmn())
                .fcstCapa(e.getFcstCapa())
                .essCg01(e.getEssCg01())
                .essCg02(e.getEssCg02())
                .essCg03(e.getEssCg03())
                .essCg04(e.getEssCg04())
                .essCg05(e.getEssCg05())
                .essCg06(e.getEssCg06())
                .essCg07(e.getEssCg07())
                .essCg08(e.getEssCg08())
                .essCg11(e.getEssCg11())
                .essCg12(e.getEssCg12())
                .essCg21(e.getEssCg21())
                .essCg22(e.getEssCg22())
                .essCg23(e.getEssCg23())
                .essCg31(e.getEssCg31())
                .essPump(e.getEssPump())
                .essQgen(e.getEssQgen())
                .essCapa(e.getEssCapa())
                .etcVari(e.getEtcVari())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
