package com.example.megacity_back.controller;


import com.example.megacity_back.dto.FcstCurtDaDto;
import com.example.megacity_back.service.JejuCurtPredictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REP_DATA_P2H_FCST_CURT_DA 데이터
@RestController
@RequestMapping("/api/jeju-curt-predict")
@RequiredArgsConstructor
public class JejuCurtPredict {

    private final JejuCurtPredictService fcstCurtDaService;

    @GetMapping("/today")
    public List<FcstCurtDaDto> getTodayCurtailment() {
        return fcstCurtDaService.getTodayCurtailment();
    }

}
