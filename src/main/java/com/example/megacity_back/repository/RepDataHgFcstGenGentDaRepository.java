package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataHgFcstGenGentDaRepository extends CrudRepository<RepDataHgFcstGenGentDa, FcstGenGentDaId> {
    List<RepDataHgFcstGenGentDa> findByAreaGrpCdAndFcstTmStartingWith(String areaGrpCd, String fcstTmPrefix);
    List<RepDataHgFcstGenGentDa> findByFcstTmStartingWith(String fcstTmPrefix);
    Optional<RepDataHgFcstGenGentDa> findFirstByAreaGrpCdAndAreaGrpIdOrderByCrtnTmDesc(String areaGrpCd, String areaGrpId);
    List<RepDataHgFcstGenGentDa> findByAreaGrpCdAndAreaGrpIdAndCrtnTmOrderByFcstTmAsc(String areaGrpCd, String areaGrpId, String crtnTm);
}
