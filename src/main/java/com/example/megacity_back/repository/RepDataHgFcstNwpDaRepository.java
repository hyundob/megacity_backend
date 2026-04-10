package com.example.megacity_back.repository;

import com.example.megacity_back.entity.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepDataHgFcstNwpDaRepository extends CrudRepository<RepDataHgFcstNwpDa, FcstNwpDaId> {
    Optional<RepDataHgFcstNwpDa> findTopByOrderByFcstTmDesc();
    List<RepDataHgFcstNwpDa> findByFcstTmBetweenOrderByFcstTmAsc(String startFcstTm, String endFcstTm); // 48시간 예측 자료료
    Optional<RepDataHgFcstNwpDa> findFirstByAreaGrpCdAndAreaGrpIdOrderByCrtnTmDesc(String areaGrpCd, String areaGrpId);
    List<RepDataHgFcstNwpDa> findByAreaGrpCdAndAreaGrpIdAndCrtnTmOrderByFcstTmAsc(String areaGrpCd, String areaGrpId, String crtnTm);
}
