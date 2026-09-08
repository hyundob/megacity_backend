package com.example.megacity_back.dto;

import com.example.megacity_back.entity.RepDataMsMeasMetShko60H;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasMetShko60HDto {

    private String stnId;
    private String tm;
    private BigDecimal wdDeg;
    private BigDecimal wd;
    private BigDecimal ws;
    private BigDecimal wsUnt;
    private BigDecimal wdGst;
    private BigDecimal wsGst;
    private BigDecimal wsGstTm;
    private BigDecimal pa;
    private BigDecimal ps;
    private BigDecimal pr;
    private BigDecimal pt;
    private BigDecimal ta;
    private BigDecimal td;
    private BigDecimal hm;
    private BigDecimal pv;
    private BigDecimal rn;
    private BigDecimal rnAcc;
    private BigDecimal sdHr1;
    private BigDecimal sdHr3;
    private BigDecimal sdDay;
    private BigDecimal sdTot;
    private BigDecimal wc;
    private BigDecimal wp;
    private BigDecimal ww;
    private BigDecimal caTot;
    private BigDecimal caMid;
    private BigDecimal chMin;
    private BigDecimal ct;
    private BigDecimal ctTop;
    private BigDecimal ctMid;
    private BigDecimal ctLow;
    private BigDecimal vs;
    private BigDecimal ssHr1;
    private BigDecimal siHr1;
    private BigDecimal tg;
    private BigDecimal stGd;
    private BigDecimal ts;
    private BigDecimal te005;
    private BigDecimal te01;
    private BigDecimal te02;
    private BigDecimal te03;
    private BigDecimal te05;
    private BigDecimal te10;
    private BigDecimal te15;
    private BigDecimal te30;
    private BigDecimal te50;
    private BigDecimal wh;
    private BigDecimal rnInt;
    private BigDecimal rnDay;
    private BigDecimal stSea;
    private BigDecimal bf;
    private BigDecimal rnJun;
    private BigDecimal ir;
    private BigDecimal ix;
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public static MeasMetShko60HDto from(RepDataMsMeasMetShko60H e) {
        return MeasMetShko60HDto.builder()
                .stnId(e.getStnId())
                .tm(e.getTm())
                .wdDeg(e.getWdDeg())
                .wd(e.getWd())
                .ws(e.getWs())
                .wsUnt(e.getWsUnt())
                .wdGst(e.getWdGst())
                .wsGst(e.getWsGst())
                .wsGstTm(e.getWsGstTm())
                .pa(e.getPa())
                .ps(e.getPs())
                .pr(e.getPr())
                .pt(e.getPt())
                .ta(e.getTa())
                .td(e.getTd())
                .hm(e.getHm())
                .pv(e.getPv())
                .rn(e.getRn())
                .rnAcc(e.getRnAcc())
                .sdHr1(e.getSdHr1())
                .sdHr3(e.getSdHr3())
                .sdDay(e.getSdDay())
                .sdTot(e.getSdTot())
                .wc(e.getWc())
                .wp(e.getWp())
                .ww(e.getWw())
                .caTot(e.getCaTot())
                .caMid(e.getCaMid())
                .chMin(e.getChMin())
                .ct(e.getCt())
                .ctTop(e.getCtTop())
                .ctMid(e.getCtMid())
                .ctLow(e.getCtLow())
                .vs(e.getVs())
                .ssHr1(e.getSsHr1())
                .siHr1(e.getSiHr1())
                .tg(e.getTg())
                .stGd(e.getStGd())
                .ts(e.getTs())
                .te005(e.getTe005())
                .te01(e.getTe01())
                .te02(e.getTe02())
                .te03(e.getTe03())
                .te05(e.getTe05())
                .te10(e.getTe10())
                .te15(e.getTe15())
                .te30(e.getTe30())
                .te50(e.getTe50())
                .wh(e.getWh())
                .rnInt(e.getRnInt())
                .rnDay(e.getRnDay())
                .stSea(e.getStSea())
                .bf(e.getBf())
                .rnJun(e.getRnJun())
                .ir(e.getIr())
                .ix(e.getIx())
                .regDate(e.getRegDate())
                .updDate(e.getUpdDate())
                .build();
    }
}
