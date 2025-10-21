package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstLfdDaDto;
import com.example.megacity_back.entity.RepDataReFcstLfdDa;
import com.example.megacity_back.repository.RepDataReFcstLfdDaRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DemandPredictService {
    private final RepDataReFcstLfdDaRepository repository;

    public List<FcstLfdDaDto> getTodayLoadForecast() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return repository.findByFcstTmStartingWith(today).stream()
                .map(e -> FcstLfdDaDto.builder()
                        .crtnTm(e.getCrtnTm())
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .build())
                .toList();
    }

    public List<FcstLfdDaDto> getLatestCrtnTmForecast() {
        // 최신 CRTN_TM 조회
        RepDataReFcstLfdDa latest = repository.findTopByOrderByCrtnTmDesc()
                .orElseThrow(() -> new RuntimeException("전력 수요 예측 데이터 없음"));

        String latestCrtnTm = latest.getCrtnTm();

        // 해당 생성시간의 모든 예측 데이터 조회
        return repository.findByCrtnTmOrderByFcstTmAsc(latestCrtnTm).stream()
                .map(e -> FcstLfdDaDto.builder()
                        .crtnTm(e.getCrtnTm())
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .build())
                .toList();
    }
}
