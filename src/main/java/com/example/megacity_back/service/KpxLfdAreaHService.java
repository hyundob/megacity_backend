package com.example.megacity_back.service;

import com.example.megacity_back.dto.KpxLfdAreaHDto;
import com.example.megacity_back.repository.RepDataReKpxLfdAreaHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpxLfdAreaHService {

    private final RepDataReKpxLfdAreaHRepository repository;

    public Page<KpxLfdAreaHDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(KpxLfdAreaHDto::from);
    }
}
