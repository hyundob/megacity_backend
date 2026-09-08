package com.example.megacity_back.controller;

import com.example.megacity_back.dto.FcstDfsShrtHDto;
import com.example.megacity_back.service.FcstDfsShrtHService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_MS_FCST_DFS_SHRT_H
@RestController
@RequestMapping("/api/fcst-dfs-shrt-h")
@RequiredArgsConstructor
public class FcstDfsShrtHController {

    private final FcstDfsShrtHService service;

    @GetMapping
    public Page<FcstDfsShrtHDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
