package com.example.megacity_back.service;

import com.example.megacity_back.dto.SukubMDto;
import com.example.megacity_back.entity.RepDataReKpxJejuSukubM;
import com.example.megacity_back.repository.RepDataReKpxJejuSukubMRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SukubMService {
    private final RepDataReKpxJejuSukubMRepository repository;
    public List<RepDataReKpxJejuSukubM> getAllSukubM() {
        return (List<RepDataReKpxJejuSukubM>) repository.findAll();
    }

    public SukubMDto getLastestSukubM() {
        RepDataReKpxJejuSukubM latest = repository.findTopByOrderByTmDesc()
                .orElseThrow(() -> new RuntimeException("데이터 없음"));

        return new SukubMDto(
                latest.getTm(),
                latest.getSuppAbility(),
                latest.getCurrPwrTot(),
                latest.getRenewPwrTot(),
                latest.getRenewPwrSolar(),
                latest.getRenewPwrWind()
        );
    }

    public List<SukubMDto> getTodaySukubM() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return repository.findByTmStartingWith(today).stream()
                .map(e -> new SukubMDto(
                        e.getTm(),
                        e.getSuppAbility(),
                        e.getCurrPwrTot(),
                        e.getRenewPwrTot(),
                        e.getRenewPwrSolar(),
                        e.getRenewPwrWind()
                ))
                .toList();
    }


}
