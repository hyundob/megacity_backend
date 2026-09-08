package com.example.megacity_back.controller;

import com.example.megacity_back.dto.MeasMetAwsHDto;
import com.example.megacity_back.service.MeasMetAwsHService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

// REP_DATA_MS_MEAS_MET_AWS_H
@RestController
@RequestMapping("/api/meas-met-aws-h")
@RequiredArgsConstructor
public class MeasMetAwsHController {

    private final MeasMetAwsHService service;

    @GetMapping
    public Page<MeasMetAwsHDto> getAll(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "100") int size) {
        return service.getAll(PageRequest.of(page, size));
    }
}
