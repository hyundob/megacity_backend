package com.example.megacity_back.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Objects;

public class KpxSmpAreaHId implements Serializable {

    private String tradeDay;
    private BigDecimal tradeHour;
    private String areaCd;

    public KpxSmpAreaHId() {}

    public KpxSmpAreaHId(String tradeDay, BigDecimal tradeHour, String areaCd) {
        this.tradeDay = tradeDay;
        this.tradeHour = tradeHour;
        this.areaCd = areaCd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpxSmpAreaHId)) return false;
        KpxSmpAreaHId that = (KpxSmpAreaHId) o;
        return Objects.equals(tradeDay, that.tradeDay) &&
                Objects.equals(tradeHour, that.tradeHour) &&
                Objects.equals(areaCd, that.areaCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeDay, tradeHour, areaCd);
    }
}
