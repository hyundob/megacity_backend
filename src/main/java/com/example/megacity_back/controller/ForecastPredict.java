package com.example.megacity_back.controller;

import com.example.megacity_back.dto.ForeCastDto;
import com.example.megacity_back.service.ForecastPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast-predict")
@RequiredArgsConstructor
public class ForecastPredict {
    private final ForecastPredictService service;

    @GetMapping("/latest")
    public ForeCastDto getLatestWeatherForecast() {
        return service.getLatestWeatherForecast();
    }

    @GetMapping("/all")
    public Page<ForeCastDto> getAllWeatherForecasts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return service.getAllWeatherForecasts(page, size);
    }

    @GetMapping("/summary")
    public Page<ForeCastDto> getWeatherForecastSummary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return service.getWeatherForecastSummary(page, size);
    }

    @GetMapping("/last-48h")
    public List<ForeCastDto> getLast48hForecast() {
        return service.getLast48hForecast();
    }
}
