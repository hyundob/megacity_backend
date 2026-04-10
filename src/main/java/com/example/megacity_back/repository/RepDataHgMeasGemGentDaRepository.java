package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataHgMeasGemGentDaRepository extends CrudRepository<RepDataHgMeasGemGentDa, GemGentDaId> {
    List<RepDataHgMeasGemGentDa> findByAreaGrpCdAndTmStartingWith(String areaGrpCd, String fcstTmPrefix);
    List<RepDataHgMeasGemGentDa> findByTmStartingWith(String tmPrefix);
    Optional<RepDataHgMeasGemGentDa> findFirstByAreaGrpCdAndAreaGrpIdOrderByTmDesc(String areaGrpCd, String areaGrpId);
}
