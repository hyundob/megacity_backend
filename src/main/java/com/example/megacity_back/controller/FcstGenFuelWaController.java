package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstGenFuelWaDto;
import com.example.megacity_back.service.FcstGenFuelWaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_FCST_GEN_FUEL_WA
@RestController
@RequestMapping("/api/fcst-gen-fuel-wa")
@RequiredArgsConstructor
public class FcstGenFuelWaController {

    private final FcstGenFuelWaService service;

    @GetMapping
    public Page<FcstGenFuelWaDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
