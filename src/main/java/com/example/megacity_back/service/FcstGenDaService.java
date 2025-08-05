package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstGenDaChartDto;
import com.example.megacity_back.entity.RepDataReFcstGenDa;
import com.example.megacity_back.repository.RepDataReFcstGenDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcstGenDaService {
    private final RepDataReFcstGenDaRepository repository;

    public List<FcstGenDaChartDto> getTodayForecasts() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<RepDataReFcstGenDa> records = repository.findByFuelTpCdAndFcstTmStartingWith("SOLAR", today);

        return records.stream()
                .map(e -> FcstGenDaChartDto.builder()
                        .fcstTm(e.getFcstTm())
                        .fcstQgen(e.getFcstQgen() != null ? e.getFcstQgen().doubleValue() : 0.0)
                        .fcstQgmx(e.getFcstQgmx() != null ? e.getFcstQgmx().doubleValue() : 0.0)
                        .fcstQgmn(e.getFcstQgmn() != null ? e.getFcstQgmn().doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
}
