package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstGenFuelWaDto;
import com.example.megacity_back.repository.RepDataReFcstGenFuelWaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcstGenFuelWaService {

    private final RepDataReFcstGenFuelWaRepository repository;

    public Page<FcstGenFuelWaDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(FcstGenFuelWaDto::from);
    }
}
