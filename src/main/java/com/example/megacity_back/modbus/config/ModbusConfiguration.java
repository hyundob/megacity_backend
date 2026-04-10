package com.example.megacity_back.modbus.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ModbusProperties.class)
public class ModbusConfiguration {
}
