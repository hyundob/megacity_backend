package com.example.megacity_back.controller;


import com.example.megacity_back.dto.GenGentDaDto;
import com.example.megacity_back.service.GenGentDaService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gen")
@RequiredArgsConstructor
public class FcstGenGentDaController {

    private final GenGentDaService fcstGenGentDaService;

    @GetMapping("/today")
    public List<GenGentDaDto> getTodayGen() {
        return fcstGenGentDaService.getTodayGen();
    }
}
