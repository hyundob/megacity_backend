package com.example.megacity_back.service;

import com.example.megacity_back.dto.GenGentDaDto;
import com.example.megacity_back.repository.RepDataHgFcstGenGentDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HgGenPredictService {
    private final RepDataHgFcstGenGentDaRepository repository;

    public List<GenGentDaDto> getTodayForecastGeneration() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return repository.findByAreaGrpCdAndFcstTmStartingWith("SEOUL", today).stream()
                .map(e -> GenGentDaDto.builder()
                        .areaGrpCd(e.getAreaGrpCd())
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstCapa(e.getFcstCapa() != null ? e.getFcstCapa().doubleValue() : 0.0)
                        .build())
                .toList();
    }
}
