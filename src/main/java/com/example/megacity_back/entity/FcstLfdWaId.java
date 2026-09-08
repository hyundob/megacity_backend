package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstLfdWaId implements Serializable {

    private String crtnTm;
    private String fcstTm;

    public FcstLfdWaId() {}

    public FcstLfdWaId(String crtnTm, String fcstTm) {
        this.crtnTm = crtnTm;
        this.fcstTm = fcstTm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstLfdWaId)) return false;
        FcstLfdWaId that = (FcstLfdWaId) o;
        return Objects.equals(crtnTm, that.crtnTm) &&
                Objects.equals(fcstTm, that.fcstTm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crtnTm, fcstTm);
    }
}
