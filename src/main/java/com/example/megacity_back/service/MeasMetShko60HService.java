package com.example.megacity_back.service;

import com.example.megacity_back.dto.MeasMetShko60HDto;
import com.example.megacity_back.repository.RepDataMsMeasMetShko60HRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasMetShko60HService {

    private final RepDataMsMeasMetShko60HRepository repository;

    public Page<MeasMetShko60HDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(MeasMetShko60HDto::from);
    }
}
