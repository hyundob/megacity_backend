package com.example.megacity_back.service;

import com.example.megacity_back.dto.MeasMetAwsHDto;
import com.example.megacity_back.repository.RepDataMsMeasMetAwsHRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeasMetAwsHService {

    private final RepDataMsMeasMetAwsHRepository repository;

    public Page<MeasMetAwsHDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(MeasMetAwsHDto::from);
    }
}
