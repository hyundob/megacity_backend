package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;


public class FcstGenGentDaId implements Serializable {

    private String AREA_GRP_CD;
    private String AREA_GRP_ID;
    private String CRTN_TM;
    private String FCST_TM;

    public FcstGenGentDaId() {}

    public FcstGenGentDaId(String AREA_GRP_CD, String AREA_GRP_ID, String CRTN_TM, String FCST_TM) {
        this.AREA_GRP_CD = AREA_GRP_CD;
        this.AREA_GRP_ID = AREA_GRP_ID;
        this.CRTN_TM = CRTN_TM;
        this.FCST_TM = FCST_TM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof FcstGenGentDaId)) return false;
        FcstGenGentDaId that = (FcstGenGentDaId) o;
        return Objects.equals(AREA_GRP_CD, that.AREA_GRP_CD) &&
                Objects.equals(AREA_GRP_ID, that.AREA_GRP_ID) &&
                Objects.equals(CRTN_TM, that.CRTN_TM) &&
                Objects.equals(FCST_TM, that.FCST_TM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AREA_GRP_CD, AREA_GRP_ID, CRTN_TM, FCST_TM);
    }
}
