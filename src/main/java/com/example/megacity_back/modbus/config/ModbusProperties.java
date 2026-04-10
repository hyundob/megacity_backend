package com.example.megacity_back.modbus.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "modbus")
public class ModbusProperties {

    private boolean enabled = false;

    @NotBlank
    private String host = "0.0.0.0";

    @Min(1)
    @Max(65535)
    private int port = 1502;

    @Min(1000)
    private int socketTimeoutMs = 5_000;

    @Min(5_000)
    private long refreshIntervalMs = 60_000L;

    @NotBlank
    private String timeZone = "Asia/Seoul";

    private boolean allowFunction03Alias = true;
}
