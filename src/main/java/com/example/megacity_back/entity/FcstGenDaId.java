package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FcstGenDaId implements Serializable {

    private String PWR_EXC_TP_CD;
    private String FUEL_TP_CD;
    private String CRTN_TM;
    private String FCST_TM;

    public FcstGenDaId() {}

    public FcstGenDaId(String PWR_EXC_TP_CD, String FUEL_TP_CD, String CRTN_TM, String FCST_TM) {
        this.PWR_EXC_TP_CD = PWR_EXC_TP_CD;
        this.FUEL_TP_CD = FUEL_TP_CD;
        this.CRTN_TM = CRTN_TM;
        this.FCST_TM = FCST_TM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof FcstGenDaId)) return false;
        FcstGenDaId that = (FcstGenDaId) o;
        return Objects.equals(PWR_EXC_TP_CD, that.PWR_EXC_TP_CD) &&
                Objects.equals(FUEL_TP_CD, that.FUEL_TP_CD)&&
                Objects.equals(CRTN_TM, that.CRTN_TM) &&
                Objects.equals(FCST_TM, that.FCST_TM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PWR_EXC_TP_CD, FUEL_TP_CD, CRTN_TM, FCST_TM);
    }

}
