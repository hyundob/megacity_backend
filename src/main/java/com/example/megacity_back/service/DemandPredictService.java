package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstLfdDaDto;
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
}
