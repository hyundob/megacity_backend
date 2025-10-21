package com.example.megacity_back.controller;

import com.example.megacity_back.service.DemandPredictService;
import com.example.megacity_back.dto.FcstLfdDaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REP_DATA_RE_FCST_LFD_DA 데이터
@RestController
@RequestMapping("/api/demand-predict")
@RequiredArgsConstructor
public class DemandPredict {

    private final DemandPredictService fcstLfdDaService;

    // @GetMapping("/today")
    // public List<FcstLfdDaDto> getTodayLoadForecast() {
    //     return fcstLfdDaService.getTodayLoadForecast();
    // }

    @GetMapping("/latest-crtn")
    public List<FcstLfdDaDto> getLatestCrtnTmForecast() {
        return fcstLfdDaService.getLatestCrtnTmForecast();
    }
}
