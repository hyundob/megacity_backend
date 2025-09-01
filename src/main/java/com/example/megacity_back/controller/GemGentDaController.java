package com.example.megacity_back.controller;

import com.example.megacity_back.dto.GemGentDaDto;
import com.example.megacity_back.service.HgGenInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hg-gen-info")
@RequiredArgsConstructor
public class GemGentDaController {

    private final HgGenInfoService gemGentDaService;

    @GetMapping("/today")
    public List<GemGentDaDto> getTodayGeneration() {
        return gemGentDaService.getTodayGeneration();
    }
}
