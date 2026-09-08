package com.example.megacity_back.entity;

import java.io.Serializable;
import java.util.Objects;

public class MeasMetShko60HId implements Serializable {

    private String stnId;
    private String tm;

    public MeasMetShko60HId() {}

    public MeasMetShko60HId(String stnId, String tm) {
        this.stnId = stnId;
        this.tm = tm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasMetShko60HId)) return false;
        MeasMetShko60HId that = (MeasMetShko60HId) o;
        return Objects.equals(stnId, that.stnId) &&
                Objects.equals(tm, that.tm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stnId, tm);
    }
}
