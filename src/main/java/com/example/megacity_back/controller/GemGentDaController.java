package com.example.megacity_back.controller;

import com.example.megacity_back.dto.GemGentDaDto;
import com.example.megacity_back.service.GemGentDaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gem")
@RequiredArgsConstructor
public class GemGentDaController {

    private final GemGentDaService gemGentDaService;

    @GetMapping("/today")
    public List<GemGentDaDto> getTodayGem() {
        return gemGentDaService.getTodayGem();
    }
}
