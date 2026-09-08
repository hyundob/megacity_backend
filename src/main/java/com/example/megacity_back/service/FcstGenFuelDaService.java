package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstGenFuelDaDto;
import com.example.megacity_back.repository.RepDataReFcstGenFuelDaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcstGenFuelDaService {

    private final RepDataReFcstGenFuelDaRepository repository;

    public Page<FcstGenFuelDaDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(FcstGenFuelDaDto::from);
    }
}
