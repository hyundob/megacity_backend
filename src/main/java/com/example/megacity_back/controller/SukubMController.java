package com.example.megacity_back.controller;

import com.example.megacity_back.dto.SukubMDto;
import com.example.megacity_back.service.SukubOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sukub-operation")
@RequiredArgsConstructor
public class SukubMController {
    private final SukubOperationService service;

    @GetMapping("/latest")
    public SukubMDto getLatestOperation() {
        return service.getLatestOperation();
    }

    @GetMapping("/today")
    public List<SukubMDto> getTodayOperation() {
        return service.getTodayOperation();
    }

}
