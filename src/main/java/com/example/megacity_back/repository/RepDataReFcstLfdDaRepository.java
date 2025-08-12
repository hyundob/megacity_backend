package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepDataReFcstLfdDaRepository extends JpaRepository<RepDataReFcstLfdDa, FcstLfdDaId> {
    List<RepDataReFcstLfdDa> findByFcstTmStartingWith(String fcstTmPrefix);
}
