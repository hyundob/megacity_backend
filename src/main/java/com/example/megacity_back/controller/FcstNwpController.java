package com.example.megacity_back.controller;

import com.example.megacity_back.dto.ForeCastDto;
import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.service.FcstNwpService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class FcstNwpController {
    private final FcstNwpService service;

    @GetMapping("/latest")
    public ForeCastDto getLatestForecast() {
        return service.getLatestForecast();
    }
    @GetMapping("/all")
    public List<RepDataHgFcstNwpDa> getallForeCast() {
        return service.getAllFcstNwp();
    }

    @GetMapping("/summary")
    public List<ForeCastDto> getSimpleForecast() {
        return service.getSimplifiedForecast();
    }
}
