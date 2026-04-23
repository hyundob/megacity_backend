package com.example.megacity_back.service;

import com.example.megacity_back.dto.ForeCastDto;
import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.exception.DataNotFoundException;
import com.example.megacity_back.repository.RepDataHgFcstNwpDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ForecastPredictService {
    private final RepDataHgFcstNwpDaRepository repository;

    public Page<ForeCastDto> getAllWeatherForecasts(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    public ForeCastDto getLatestWeatherForecast() {
        RepDataHgFcstNwpDa latest = repository.findTopByOrderByFcstTmDesc()
                .orElseThrow(() -> new DataNotFoundException("예보 데이터 없음"));
        return toDto(latest);
    }

    public Page<ForeCastDto> getWeatherForecastSummary(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).map(this::toDto);
    }

    public java.util.List<ForeCastDto> getLast48hForecast() {
        RepDataHgFcstNwpDa latest = repository.findTopByOrderByFcstTmDesc()
                .orElseThrow(() -> new DataNotFoundException("예보 데이터 없음"));

        String latestFcst = latest.getFcstTm();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String startFcst = LocalDateTime.parse(latestFcst, formatter).minusHours(48).format(formatter);

        return repository.findByFcstTmBetweenOrderByFcstTmAsc(startFcst, latestFcst)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ForeCastDto toDto(RepDataHgFcstNwpDa e) {
        return new ForeCastDto(
                e.getCrtnTm(),
                e.getFcstTm(),
                e.getAreaGrpId(),
                e.getFcstSrad(),
                e.getFcstTemp(),
                e.getFcstHumi(),
                e.getFcstWspd(),
                e.getFcstPsfc()
        );
    }
}
