package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstLfdWaDto;
import com.example.megacity_back.repository.RepDataReFcstLfdWaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcstLfdWaService {

    private final RepDataReFcstLfdWaRepository repository;

    public Page<FcstLfdWaDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(FcstLfdWaDto::from);
    }
}
