package com.example.megacity_back.service;

import com.example.megacity_back.dto.GemGentDaDto;
import com.example.megacity_back.repository.RepDataHgMeasGemGentDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HgGenInfoService {
    private final RepDataHgMeasGemGentDaRepository repository;

    public List<GemGentDaDto> getTodayGeneration() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return repository.findByAreaGrpCdAndTmStartingWith("SEOUL", today).stream()
                .map(e -> GemGentDaDto.builder()
                        .areaGrpCd(e.getAreaGrpCd())
                        .tm(e.getTm())
                        .hgenProd(e.getHgenProd() != null ? e.getHgenProd().doubleValue() : 0.0)
                        .hgenCapa(e.getHgenCapa() != null ? e.getHgenCapa().doubleValue() : 0.0)
                        .build())
                .toList();
    }
}
