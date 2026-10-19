package com.example.megacity_back.hydrogen;

import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;

final class HydrogenTime {
    static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    static final DateTimeFormatter TM = DateTimeFormatter.ofPattern("uuuuMMddHHmm").withResolverStyle(ResolverStyle.STRICT);
    static String format(LocalDateTime t) { return t.format(TM); }
    static LocalDateTime parse(String t) { return LocalDateTime.parse(t, TM); }
    static LocalDateTime hour(LocalDateTime t) { return t.truncatedTo(ChronoUnit.HOURS); }
    static LocalDateTime fiveMinutes(LocalDateTime t) {
        return t.truncatedTo(ChronoUnit.MINUTES).withMinute(t.getMinute() / 5 * 5);
    }
    static void wholeFiveMinutes(LocalDateTime t) {
        if (t == null || !t.equals(fiveMinutes(t))) throw new IllegalArgumentException("시각은 5분 경계여야 합니다.");
    }
    static LocalDateTime minuteCeiling(LocalDateTime t) {
        var minute = t.truncatedTo(ChronoUnit.MINUTES);
        return t.equals(minute) ? minute : minute.plusMinutes(1);
    }
    static void wholeHour(LocalDateTime t) {
        if (t == null || !t.equals(hour(t))) throw new IllegalArgumentException("시각은 정시여야 합니다.");
    }
    static void wholeMinute(LocalDateTime t) {
        if (t == null || t.getSecond() != 0 || t.getNano() != 0) throw new IllegalArgumentException("지령 시각은 분 단위여야 합니다.");
    }
}
