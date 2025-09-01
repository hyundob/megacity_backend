package com.example.megacity_back.controller;

import com.example.megacity_back.service.DemandPredictService;
import com.example.megacity_back.dto.FcstLfdDaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demand-predict")
@RequiredArgsConstructor
public class FcstLfdDaController {

    private final DemandPredictService fcstLfdDaService;

    @GetMapping("/today")
    public List<FcstLfdDaDto> getTodayLoadForecast() {
        return fcstLfdDaService.getTodayLoadForecast();
    }
}
