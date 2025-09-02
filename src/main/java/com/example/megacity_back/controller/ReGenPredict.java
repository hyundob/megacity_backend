package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstGenDaChartDto;
import com.example.megacity_back.service.ReGenPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REP_DATA_RE_FCST_GEN_DA 데이터
@RestController
@RequestMapping("/api/re-gen-predict")
@RequiredArgsConstructor
public class ReGenPredict {

    private final ReGenPredictService service;

    @GetMapping("/today")
    public List<FcstGenDaChartDto> getTodayForecastGenerationChart() {
        return service.getTodayForecastGenerationChart();
    }

    @GetMapping("/ess")
    public List<FcstGenDaChartDto> getTodayEssOperation() {
        return service.getTodayForecastGenerationChart();
    }
}
