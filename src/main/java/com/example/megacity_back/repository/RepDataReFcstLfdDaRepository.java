package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataReFcstLfdDaRepository extends JpaRepository<RepDataReFcstLfdDa, FcstLfdDaId> {
    List<RepDataReFcstLfdDa> findByFcstTmStartingWith(String fcstTmPrefix);
    Optional<RepDataReFcstLfdDa> findTopByOrderByCrtnTmDesc();
    List<RepDataReFcstLfdDa> findByCrtnTmOrderByFcstTmAsc(String crtnTm);
}
