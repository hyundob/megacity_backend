package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstDfsShrtHId implements Serializable {

    private String stnId;
    private String crtnTm;
    private String fcstTm;

    public FcstDfsShrtHId() {}

    public FcstDfsShrtHId(String stnId, String crtnTm, String fcstTm) {
        this.stnId = stnId;
        this.crtnTm = crtnTm;
        this.fcstTm = fcstTm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstDfsShrtHId)) return false;
        FcstDfsShrtHId that = (FcstDfsShrtHId) o;
        return Objects.equals(stnId, that.stnId) &&
                Objects.equals(crtnTm, that.crtnTm) &&
                Objects.equals(fcstTm, that.fcstTm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stnId, crtnTm, fcstTm);
    }
}
