package com.example.megacity_back.controller;

import com.example.megacity_back.dto.MeasMetShko60HDto;
import com.example.megacity_back.service.MeasMetShko60HService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_MS_MEAS_MET_SHKO60_H
@RestController
@RequestMapping("/api/meas-met-shko60-h")
@RequiredArgsConstructor
public class MeasMetShko60HController {

    private final MeasMetShko60HService service;

    @GetMapping
    public Page<MeasMetShko60HDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
