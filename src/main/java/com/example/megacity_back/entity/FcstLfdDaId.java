package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstLfdDaId implements Serializable {

    private String CRTN_TM;
    private String FCST_TM;

    public FcstLfdDaId() {}

    public FcstLfdDaId(String CRTN_TM, String FCST_TM) {
        this.CRTN_TM = CRTN_TM;
        this.FCST_TM = FCST_TM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcstLfdDaId)) return false;
        FcstLfdDaId that = (FcstLfdDaId) o;
        return Objects.equals(CRTN_TM, that.CRTN_TM) &&
                Objects.equals(FCST_TM, that.FCST_TM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(CRTN_TM, FCST_TM);
    }
}
