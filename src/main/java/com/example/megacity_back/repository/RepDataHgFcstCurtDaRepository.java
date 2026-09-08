package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataHgFcstCurtDaRepository extends CrudRepository<RepDataHgFcstCurtDa, FcstCurtDaId> {
    List<RepDataHgFcstCurtDa> findByFcstTmStartingWith(String fcstTmPrefix);
    Optional<RepDataHgFcstCurtDa> findTopByOrderByCrtnTmDesc();
    List<RepDataHgFcstCurtDa> findByCrtnTmOrderByFcstTmAsc(String crtnTm);
}
