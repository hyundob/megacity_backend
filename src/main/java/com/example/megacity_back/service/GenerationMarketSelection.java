package com.example.megacity_back.service;

import com.example.megacity_back.exception.DataNotFoundException;
import com.example.megacity_back.repository.RepDataReFcstGenDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerationMarketSelection {
    private final RepDataReFcstGenDaRepository repository;
    @Value("${megacity.market-code:}")
    private String configuredCode = "";

    public String resolve() {
        if (!configuredCode.isBlank()) return configuredCode.trim();
        var codes = repository.findDistinctMarketCodes();
        if (codes.isEmpty()) throw new DataNotFoundException("발전 예측 데이터 없음");
        if (codes.size() != 1) throw new IllegalStateException(
                "Multiple power markets found. Set MEGACITY_MARKET_CODE to the exact source code.");
        return codes.get(0);
    }
}
