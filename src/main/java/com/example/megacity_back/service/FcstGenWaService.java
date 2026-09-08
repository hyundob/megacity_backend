package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstGenWaDto;
import com.example.megacity_back.repository.RepDataReFcstGenWaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcstGenWaService {

    private final RepDataReFcstGenWaRepository repository;

    public Page<FcstGenWaDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(FcstGenWaDto::from);
    }
}
