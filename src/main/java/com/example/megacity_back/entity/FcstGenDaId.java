package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstGenDaId implements Serializable {

    private String pwrExcTpCd;
    private String fuelTpCd;
    private String crtnTm;
    private String fcstTm;

    public FcstGenDaId() {}

    public FcstGenDaId(String pwrExcTpCd, String fuelTpCd, String crtnTm, String fcstTm) {
        this.pwrExcTpCd = pwrExcTpCd;
        this.fuelTpCd = fuelTpCd;
        this.crtnTm = crtnTm;
        this.fcstTm = fcstTm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstGenDaId)) return false;
        FcstGenDaId that = (FcstGenDaId) o;
        return Objects.equals(pwrExcTpCd, that.pwrExcTpCd) &&
                Objects.equals(fuelTpCd, that.fuelTpCd) &&
                Objects.equals(crtnTm, that.crtnTm) &&
                Objects.equals(fcstTm, that.fcstTm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pwrExcTpCd, fuelTpCd, crtnTm, fcstTm);
    }
}
