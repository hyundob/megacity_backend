package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataReFcstGenDaRepository extends CrudRepository<RepDataReFcstGenDa, FcstGenDaId> {
    @org.springframework.data.jpa.repository.Query("select distinct r.pwrExcTpCd from RepDataReFcstGenDa r order by r.pwrExcTpCd")
    List<String> findDistinctMarketCodes();
    List<RepDataReFcstGenDa> findByFuelTpCdAndFcstTmStartingWith(String fuelTpCd, String fcstTmStartingWith);
    List<RepDataReFcstGenDa> findByFuelTpCdOrderByFcstTmDesc(String fuelTpCd, Pageable pageable);
    Optional<RepDataReFcstGenDa> findTopByFuelTpCdOrderByCrtnTmDesc(String fuelTpCd);
    List<RepDataReFcstGenDa> findByFuelTpCdAndCrtnTmOrderByFcstTmAsc(String fuelTpCd, String crtnTm);
    Optional<RepDataReFcstGenDa> findTopByPwrExcTpCdAndFuelTpCdOrderByCrtnTmDesc(String market, String fuel);
    List<RepDataReFcstGenDa> findByPwrExcTpCdAndFuelTpCdAndCrtnTmOrderByFcstTmAsc(String market, String fuel, String createdAt);
}
