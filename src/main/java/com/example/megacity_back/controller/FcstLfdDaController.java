package com.example.megacity_back.controller;

import com.example.megacity_back.service.FcstLfdDaService;
import com.example.megacity_back.dto.FcstLfdDaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lfd")
@RequiredArgsConstructor
public class FcstLfdDaController {

    private final FcstLfdDaService fcstLfdDaService;

    @GetMapping("/demand-today")
    public List<FcstLfdDaDto> getDemandTodayLfd() {
        return fcstLfdDaService.getFcstLfdDa();
    }
}
