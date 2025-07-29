package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstGenDaChartDto;
import com.example.megacity_back.service.FcstGenDaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fcst-gen")
@RequiredArgsConstructor
public class FcstGenDaController {

    private final FcstGenDaService service;

    @GetMapping("/chart")
    public List<FcstGenDaChartDto> getTodayFcstGenChart() {
        return service.getTodayForecasts();
    }
}
