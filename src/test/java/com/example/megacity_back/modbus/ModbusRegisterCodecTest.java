package com.example.megacity_back.modbus;

import com.example.megacity_back.modbus.model.ModbusRegisterCodec;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class ModbusRegisterCodecTest {

    @Test
    void encodeFloat32UsesCanonicalNaNForNull() {
        int[] registers = ModbusRegisterCodec.encodeFloat32(null);

        assertThat(registers).containsExactly(0x7FC0, 0x0000);
    }

    @Test
    void parseLeadMinutesSupportsHhhMmFormat() {
        assertThat(ModbusRegisterCodec.parseLeadMinutes("00130")).isEqualTo(90);
    }

    @Test
    void parseEpochSecondsUsesConfiguredZone() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        String timestamp = "202604091530";

        long expected = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
                .atZone(zoneId)
                .toEpochSecond();

        assertThat(ModbusRegisterCodec.parseEpochSeconds(timestamp, zoneId)).isEqualTo(expected);
    }
}
