package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepDataHgMeasGemGentDaRepository extends CrudRepository<RepDataHgMeasGemGentDa, GemGentDaId> {
    List<RepDataHgMeasGemGentDa> findByAreaGrpCdAndTmStartingWith(String areaGrpCd, String fcstTmPrefix);
}
