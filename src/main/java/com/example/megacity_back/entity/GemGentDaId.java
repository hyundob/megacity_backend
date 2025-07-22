package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class GemGentDaId implements Serializable {

    private String TM;
    private String AREA_GRP_CD;
    private String AREA_GRP_ID;

    public GemGentDaId() {}

    public GemGentDaId(String TM, String AREA_GRP_CD, String AREA_GRP_ID) {
        this.TM = TM;
        this.AREA_GRP_CD = AREA_GRP_CD;
        this.AREA_GRP_ID = AREA_GRP_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof GemGentDaId)) return false;
        GemGentDaId that = (GemGentDaId) o;
        return Objects.equals(TM, that.TM) &&
                Objects.equals(AREA_GRP_CD, that.AREA_GRP_CD) &&
                Objects.equals(AREA_GRP_ID, that.AREA_GRP_ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(TM, AREA_GRP_CD, AREA_GRP_ID);
    }
}
