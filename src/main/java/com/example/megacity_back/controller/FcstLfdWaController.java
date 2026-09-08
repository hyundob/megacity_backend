package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstLfdWaDto;
import com.example.megacity_back.service.FcstLfdWaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_FCST_LFD_WA
@RestController
@RequestMapping("/api/fcst-lfd-wa")
@RequiredArgsConstructor
public class FcstLfdWaController {

    private final FcstLfdWaService service;

    @GetMapping
    public Page<FcstLfdWaDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
