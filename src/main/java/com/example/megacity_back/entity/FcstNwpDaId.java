package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstNwpDaId implements Serializable {

    private String pwrExcTpCd;
    private String areaGrpCd;
    private String areaGrpId;
    private String crtnTm;
    private String fcstTm;

    public FcstNwpDaId() {}

    public FcstNwpDaId(String pwrExcTpCd, String areaGrpCd, String areaGrpId, String crtnTm, String fcstTm) {
        this.pwrExcTpCd = pwrExcTpCd;
        this.areaGrpCd = areaGrpCd;
        this.areaGrpId = areaGrpId;
        this.crtnTm = crtnTm;
        this.fcstTm = fcstTm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstNwpDaId)) return false;
        FcstNwpDaId that = (FcstNwpDaId) o;
        return Objects.equals(pwrExcTpCd, that.pwrExcTpCd) &&
                Objects.equals(areaGrpCd, that.areaGrpCd) &&
                Objects.equals(areaGrpId, that.areaGrpId) &&
                Objects.equals(crtnTm, that.crtnTm) &&
                Objects.equals(fcstTm, that.fcstTm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pwrExcTpCd, areaGrpCd, areaGrpId, crtnTm, fcstTm);
    }
}
