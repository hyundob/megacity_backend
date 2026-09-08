package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstGenFuelDaDto;
import com.example.megacity_back.service.FcstGenFuelDaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_FCST_GEN_FUEL_DA
@RestController
@RequestMapping("/api/fcst-gen-fuel-da")
@RequiredArgsConstructor
public class FcstGenFuelDaController {

    private final FcstGenFuelDaService service;

    @GetMapping
    public Page<FcstGenFuelDaDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
