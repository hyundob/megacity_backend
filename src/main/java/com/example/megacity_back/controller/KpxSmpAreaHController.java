package com.example.megacity_back.controller;

import com.example.megacity_back.dto.KpxSmpAreaHDto;
import com.example.megacity_back.service.KpxSmpAreaHService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_KPX_SMP_AREA_H
@RestController
@RequestMapping("/api/kpx-smp-area-h")
@RequiredArgsConstructor
public class KpxSmpAreaHController {

    private final KpxSmpAreaHService service;

    @GetMapping
    public Page<KpxSmpAreaHDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
