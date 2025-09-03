package com.example.megacity_back.controller;

import com.example.megacity_back.service.JejuWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jeju-weather")
public class JejuWeatherController {
    private final JejuWeatherService service;

    public JejuWeatherController(JejuWeatherService service) {
        this.service = service;
    }

    /** 제주시: 실황(T1H/PTY/VEC/WSD) + 예보(SKY/PTY) 묶음 */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> current() {
        return ResponseEntity.ok(service.fetchMergedNow());
    }
}