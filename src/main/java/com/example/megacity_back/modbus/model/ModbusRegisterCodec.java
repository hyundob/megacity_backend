package com.example.megacity_back.modbus.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ModbusRegisterCodec {

    public static final int UINT16_NULL = 0xFFFF;
    public static final long UINT32_NULL = 0xFFFF_FFFFL;
    public static final int FLOAT32_NULL_BITS = Float.floatToIntBits(Float.NaN);

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private ModbusRegisterCodec() {
    }

    public static int encodeUint16(Integer value) {
        return value == null ? UINT16_NULL : value & 0xFFFF;
    }

    public static int[] encodeUint32(Long value) {
        long normalized = value == null ? UINT32_NULL : value & UINT32_NULL;
        return new int[]{
                (int) ((normalized >>> 16) & 0xFFFF),
                (int) (normalized & 0xFFFF)
        };
    }

    public static int[] encodeFloat32(BigDecimal value) {
        int bits = value == null ? FLOAT32_NULL_BITS : Float.floatToIntBits(value.floatValue());
        return new int[]{
                (bits >>> 16) & 0xFFFF,
                bits & 0xFFFF
        };
    }

    public static Long parseEpochSeconds(String value, ZoneId zoneId) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(value.trim(), TIME_FORMATTER);
            return dateTime.atZone(zoneId).toEpochSecond();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    public static Integer parseLeadMinutes(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();
        if (!normalized.chars().allMatch(Character::isDigit)) {
            return null;
        }

        try {
            if (normalized.length() >= 4) {
                String hourPart = normalized.substring(0, normalized.length() - 2);
                String minutePart = normalized.substring(normalized.length() - 2);
                return Integer.parseInt(hourPart) * 60 + Integer.parseInt(minutePart);
            }
            return Integer.parseInt(normalized);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer parseUint16(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
