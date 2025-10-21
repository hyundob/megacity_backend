package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstGenDaChartDto;
import com.example.megacity_back.entity.RepDataReFcstGenDa;
import com.example.megacity_back.repository.RepDataReFcstGenDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReGenPredictService {
    private final RepDataReFcstGenDaRepository repository;

    public List<FcstGenDaChartDto> getTodayForecastGenerationChart() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<RepDataReFcstGenDa> records = repository.findByFuelTpCdAndFcstTmStartingWith("SOLAR", today);

        return records.stream()
                .map(e -> FcstGenDaChartDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .fcstCapa(e.getFcstCapa() != null ? e.getFcstCapa().doubleValue() : 0.0)
                        .essChrg(e.getEssChrg() != null ? e.getEssChrg().doubleValue() : 0.0)
                        .essDisc(e.getEssDisc() != null ? e.getEssDisc().doubleValue() : 0.0)
                        .essCapa(e.getEssCapa() != null ? e.getEssCapa().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

    public List<FcstGenDaChartDto> getLast49SolarForecast() {
        // FUEL_TP_CD = 13 (태양광), 최신 CRTN_TM의 모든 예측 데이터
        RepDataReFcstGenDa latest = repository.findTopByFuelTpCdOrderByCrtnTmDesc("13")
                .orElseThrow(() -> new RuntimeException("태양광 예측 데이터 없음"));

        String latestCrtnTm = latest.getCrtnTm();

        // 해당 생성시간의 모든 예측 데이터 조회
        List<RepDataReFcstGenDa> records = repository.findByFuelTpCdAndCrtnTmOrderByFcstTmAsc("13", latestCrtnTm);

        return records.stream()
                .map(e -> FcstGenDaChartDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .fcstCapa(e.getFcstCapa() != null ? e.getFcstCapa().doubleValue() : 0.0)
                        .essChrg(e.getEssChrg() != null ? e.getEssChrg().doubleValue() : 0.0)
                        .essDisc(e.getEssDisc() != null ? e.getEssDisc().doubleValue() : 0.0)
                        .essCapa(e.getEssCapa() != null ? e.getEssCapa().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

    public List<FcstGenDaChartDto> getLatestWindForecast() {
        // FUEL_TP_CD = 16 (풍력), 최신 CRTN_TM의 모든 예측 데이터
        RepDataReFcstGenDa latest = repository.findTopByFuelTpCdOrderByCrtnTmDesc("16")
                .orElseThrow(() -> new RuntimeException("풍력 예측 데이터 없음"));

        String latestCrtnTm = latest.getCrtnTm();

        // 해당 생성시간의 모든 예측 데이터 조회
        List<RepDataReFcstGenDa> records = repository.findByFuelTpCdAndCrtnTmOrderByFcstTmAsc("16", latestCrtnTm);

        return records.stream()
                .map(e -> FcstGenDaChartDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .fcstCapa(e.getFcstCapa() != null ? e.getFcstCapa().doubleValue() : 0.0)
                        .essChrg(e.getEssChrg() != null ? e.getEssChrg().doubleValue() : 0.0)
                        .essDisc(e.getEssDisc() != null ? e.getEssDisc().doubleValue() : 0.0)
                        .essCapa(e.getEssCapa() != null ? e.getEssCapa().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
}
