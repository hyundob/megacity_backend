package com.example.megacity_back.controller;

import com.example.megacity_back.dto.GemGentDaDto;
import com.example.megacity_back.service.HgGenInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// REP_DATA_HG_MEAS_GEM_GENT_DA 데이터
@RestController
@RequestMapping("/api/hg-gen-info")
@RequiredArgsConstructor
public class HgGenInfo {

    private final HgGenInfoService gemGentDaService;

    @GetMapping("/today")
    public List<GemGentDaDto> getTodayGeneration() {
        return gemGentDaService.getTodayGeneration();
    }
}
