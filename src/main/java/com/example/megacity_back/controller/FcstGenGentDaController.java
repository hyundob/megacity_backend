package com.example.megacity_back.controller;


import com.example.megacity_back.dto.GenGentDaDto;
import com.example.megacity_back.service.HgGenPredictService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hg-gen-predict")
@RequiredArgsConstructor
public class FcstGenGentDaController {

    private final HgGenPredictService fcstGenGentDaService;

    @GetMapping("/today")
    public List<GenGentDaDto> getTodayForecastGeneration() {
        return fcstGenGentDaService.getTodayForecastGeneration();
    }
}
