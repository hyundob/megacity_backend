package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;


public class FcstGenGentDaId implements Serializable {

    private String areaGrpCd;
    private String areaGrpId;
    private String crtnTm;
    private String fcstTm;

    public FcstGenGentDaId() {}

    public FcstGenGentDaId(String areaGrpCd, String areaGrpId, String crtnTm, String fcstTm) {
        this.areaGrpCd = areaGrpCd;
        this.areaGrpId = areaGrpId;
        this.crtnTm = crtnTm;
        this.fcstTm = fcstTm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstGenGentDaId)) return false;
        FcstGenGentDaId that = (FcstGenGentDaId) o;
        return Objects.equals(areaGrpCd, that.areaGrpCd) &&
                Objects.equals(areaGrpId, that.areaGrpId) &&
                Objects.equals(crtnTm, that.crtnTm) &&
                Objects.equals(fcstTm, that.fcstTm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaGrpCd, areaGrpId, crtnTm, fcstTm);
    }
}