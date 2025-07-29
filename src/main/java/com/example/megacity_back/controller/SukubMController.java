package com.example.megacity_back.controller;

import com.example.megacity_back.dto.SukubMDto;
import com.example.megacity_back.service.SukubMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation")
@RequiredArgsConstructor
public class SukubMController {
    private final SukubMService service;

    @GetMapping("/latest")
    public SukubMDto getLatestSukubM() {
        return service.getLastestSukubM();
    }

}
