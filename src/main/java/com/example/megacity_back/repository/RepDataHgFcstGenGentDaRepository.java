package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepDataHgFcstGenGentDaRepository extends CrudRepository<RepDataHgFcstGenGentDa, FcstGenGentDaId> {
    List<RepDataHgFcstGenGentDa> findByAreaGrpCdAndFcstTmStartingWith(String areaGrpCd, String fcstTmPrefix);
}
