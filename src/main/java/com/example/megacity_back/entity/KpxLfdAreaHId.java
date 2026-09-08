package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class KpxLfdAreaHId implements Serializable {

    private String tradeDay;
    private String areaCd;

    public KpxLfdAreaHId() {}

    public KpxLfdAreaHId(String tradeDay, String areaCd) {
        this.tradeDay = tradeDay;
        this.areaCd = areaCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpxLfdAreaHId)) return false;
        KpxLfdAreaHId that = (KpxLfdAreaHId) o;
        return Objects.equals(tradeDay, that.tradeDay) &&
                Objects.equals(areaCd, that.areaCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeDay, areaCd);
    }
}
