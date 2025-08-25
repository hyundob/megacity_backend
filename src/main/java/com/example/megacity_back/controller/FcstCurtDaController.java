package com.example.megacity_back.controller;


import com.example.megacity_back.dto.FcstCurtDaDto;
import com.example.megacity_back.service.FcstCurtDaService;
import com.example.megacity_back.service.FcstLfdDaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curt")
@RequiredArgsConstructor
public class FcstCurtDaController {

    private final FcstCurtDaService fcstCurtDaService;

    @GetMapping("/today")
    public List<FcstCurtDaDto> getTodayCurt() {
        return fcstCurtDaService.getTodayDate();
    }

}
