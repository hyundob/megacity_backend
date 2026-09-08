package com.example.megacity_back.service;

import com.example.megacity_back.dto.FcstDfsShrtHDto;
import com.example.megacity_back.repository.RepDataMsFcstDfsShrtHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcstDfsShrtHService {

    private final RepDataMsFcstDfsShrtHRepository repository;

    public Page<FcstDfsShrtHDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(FcstDfsShrtHDto::from);
    }
}
