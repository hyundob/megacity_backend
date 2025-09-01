package com.example.megacity_back.service;

import com.example.megacity_back.dto.ForeCastDto;
import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.repository.RepDataHgFcstNwpDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ForecastPredictService {
    private final RepDataHgFcstNwpDaRepository repository;

    public List<RepDataHgFcstNwpDa> getAllWeatherForecasts() {
        return (List<RepDataHgFcstNwpDa>) repository.findAll();
    }

    public ForeCastDto getLatestWeatherForecast() {
    RepDataHgFcstNwpDa latest = repository.findTopByOrderByFcstTmDesc()
            .orElseThrow(() -> new RuntimeException("예보 데이터 없음"));

        return new ForeCastDto(
                latest.getCrtnTm(),
                latest.getFcstTm(),
                latest.getFcstSrad(),
                latest.getFcstTemp(),
                latest.getFcstHumi(),
                latest.getFcstWspd(),
                latest.getFcstPsfc()
        );
    }

    public List<ForeCastDto> getWeatherForecastSummary() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(e -> new ForeCastDto(
                        e.getCrtnTm(),
                        e.getFcstTm(),
                        e.getFcstSrad(),
                        e.getFcstTemp(),
                        e.getFcstHumi(),
                        e.getFcstWspd(),
                        e.getFcstPsfc()
                ))
                .collect(Collectors.toList());
    }
}
