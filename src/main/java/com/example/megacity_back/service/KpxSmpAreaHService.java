package com.example.megacity_back.service;

import com.example.megacity_back.dto.KpxSmpAreaHDto;
import com.example.megacity_back.repository.RepDataReKpxSmpAreaHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpxSmpAreaHService {

    private final RepDataReKpxSmpAreaHRepository repository;

    public Page<KpxSmpAreaHDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(KpxSmpAreaHDto::from);
    }
}
