package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class GemGentDaId implements Serializable {

    private String tm;
    private String areaGrpCd;
    private String areaGrpId;

    public GemGentDaId() {}

    public GemGentDaId(String tm, String areaGrpCd, String areaGrpId) {
        this.tm = tm;
        this.areaGrpCd = areaGrpCd;
        this.areaGrpId = areaGrpId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GemGentDaId)) return false;
        GemGentDaId that = (GemGentDaId) o;
        return Objects.equals(tm, that.tm) &&
                Objects.equals(areaGrpCd, that.areaGrpCd) &&
                Objects.equals(areaGrpId, that.areaGrpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tm, areaGrpCd, areaGrpId);
    }
}
