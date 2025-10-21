package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstCurtDaDto;
import com.example.megacity_back.entity.RepDataP2hFcstCurtDa;
import com.example.megacity_back.repository.RepDataP2hFcstCurtDaRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JejuCurtPredictService {
    private final RepDataP2hFcstCurtDaRepository repository;
    public List<FcstCurtDaDto> getTodayCurtailment() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return repository.findByFcstTmStartingWith(today).stream()
                .map(e -> FcstCurtDaDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstMinpw(e.getFcstMinpw() != null ? e.getFcstMinpw().doubleValue() : 0.0)
                        .fcstCurt(e.getFcstCurt() != null ? e.getFcstCurt().doubleValue() : 0.0)
                        .build())
                .toList();
    }

    public List<FcstCurtDaDto> getLatestCrtnCurtailment() {
        // 최신 CRTN_TM 조회
        RepDataP2hFcstCurtDa latest = repository.findTopByOrderByCrtnTmDesc()
                .orElseThrow(() -> new RuntimeException("출력제어 예측 데이터 없음"));

        String latestCrtnTm = latest.getCrtnTm();

        // 해당 생성시간의 모든 예측 데이터 조회
        return repository.findByCrtnTmOrderByFcstTmAsc(latestCrtnTm).stream()
                .map(e -> FcstCurtDaDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstMinpw(e.getFcstMinpw() != null ? e.getFcstMinpw().doubleValue() : 0.0)
                        .fcstCurt(e.getFcstCurt() != null ? e.getFcstCurt().doubleValue() : 0.0)
                        .build())
                .toList();
    }
}
