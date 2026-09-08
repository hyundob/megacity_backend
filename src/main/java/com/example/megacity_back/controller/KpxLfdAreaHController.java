package com.example.megacity_back.controller;

import com.example.megacity_back.dto.KpxLfdAreaHDto;
import com.example.megacity_back.service.KpxLfdAreaHService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_RE_KPX_LFD_AREA_H
@RestController
@RequestMapping("/api/kpx-lfd-area-h")
@RequiredArgsConstructor
public class KpxLfdAreaHController {

    private final KpxLfdAreaHService service;

    @GetMapping
    public Page<KpxLfdAreaHDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
