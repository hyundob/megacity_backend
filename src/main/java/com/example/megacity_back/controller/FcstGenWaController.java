package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstGenWaDto;
import com.example.megacity_back.service.FcstGenWaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_FCST_GEN_WA
@RestController
@RequestMapping("/api/fcst-gen-wa")
@RequiredArgsConstructor
public class FcstGenWaController {

    private final FcstGenWaService service;

    @GetMapping
    public Page<FcstGenWaDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
