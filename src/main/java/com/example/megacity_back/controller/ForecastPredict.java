package com.example.megacity_back.controller;

import com.example.megacity_back.dto.ForeCastDto;
import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.service.ForecastPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REP_DATA_HG_FCST_NWP_DA 데이터
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
    public List<RepDataHgFcstNwpDa> getAllWeatherForecasts() {
        return service.getAllWeatherForecasts();
    }

    @GetMapping("/summary")
    public List<ForeCastDto> getWeatherForecastSummary() {
        return service.getWeatherForecastSummary();
    }
}
