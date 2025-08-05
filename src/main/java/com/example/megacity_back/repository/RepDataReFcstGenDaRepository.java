package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepDataReFcstGenDaRepository extends CrudRepository<RepDataReFcstGenDa, FcstGenGentDaId> {
    List<RepDataReFcstGenDa> findByFuelTpCdAndFcstTmStartingWith(String fuelTpCd, String fcstTmStartingWith);
}
