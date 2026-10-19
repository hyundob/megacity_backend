package com.example.megacity_back.modbus.service;

import com.example.megacity_back.entity.RepDataHgFcstCurtDa;
import com.example.megacity_back.entity.RepDataReFcstGenDa;
import com.example.megacity_back.entity.RepDataReFcstLfdDa;
import com.example.megacity_back.entity.RepDataReKpxJejuSukubM;
import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.model.GroupDefinition;
import com.example.megacity_back.modbus.model.ModbusRegisterCodec;
import com.example.megacity_back.modbus.model.ModbusRegisterMap;
import com.example.megacity_back.repository.RepDataHgFcstCurtDaRepository;
import com.example.megacity_back.repository.RepDataReFcstGenDaRepository;
import com.example.megacity_back.repository.RepDataReFcstLfdDaRepository;
import com.example.megacity_back.repository.RepDataReKpxJejuSukubMRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "modbus", name = "enabled", havingValue = "true")
public class ModbusRegisterSnapshotService {

    private static final int COMMON_START = 30001;
    private static final int HYDROGEN_ACTUAL_START = 30101;
    private static final int HYDROGEN_ACTUAL_BLOCK = 10;
    private static final int HYDROGEN_FORECAST_START = 30501;
    private static final int HYDROGEN_FORECAST_BLOCK = 590;
    private static final int WEATHER_START = 53001;
    private static final int WEATHER_BLOCK = 982;
    private static final int JEJU_OPERATION_START = 90401;
    private static final int JEJU_CURT_START = 90421;
    private static final int JEJU_CURT_BLOCK = 394;
    private static final int DEMAND_START = 90821;
    private static final int DEMAND_BLOCK = 1080;
    private static final int RENEWABLE_SOLAR_START = 91921;
    private static final int RENEWABLE_WIND_START = 93421;
    private static final int RENEWABLE_BLOCK = 1500;
    private static final int POWER_MARKET_CODE = 9;
    private static final int SOLAR_FUEL_CODE = 13;
    private static final int WIND_FUEL_CODE = 16;

    private static final List<GroupDefinition> GROUPS = List.of(
            new GroupDefinition(1, "01", "1968_005"),
            new GroupDefinition(2, "01", "1969_016"),
            new GroupDefinition(3, "01", "4593_001"),
            new GroupDefinition(4, "01", "4593_002"),
            new GroupDefinition(5, "01", "4593_003"),
            new GroupDefinition(6, "01", "4593_004"),
            new GroupDefinition(7, "01", "4593_005"),
            new GroupDefinition(8, "01", "4593_006"),
            new GroupDefinition(9, "01", "4593_007"),
            new GroupDefinition(10, "01", "4593_008"),
            new GroupDefinition(11, "01", "4593_009"),
            new GroupDefinition(12, "01", "4593_010"),
            new GroupDefinition(13, "01", "4593_011"),
            new GroupDefinition(14, "01", "4593_012"),
            new GroupDefinition(15, "01", "4593_013"),
            new GroupDefinition(16, "01", "4593_014"),
            new GroupDefinition(17, "01", "4593_015"),
            new GroupDefinition(18, "01", "8711_001"),
            new GroupDefinition(19, "01", "8711_002"),
            new GroupDefinition(20, "01", "8711_003"),
            new GroupDefinition(21, "01", "8711_004"),
            new GroupDefinition(22, "01", "8711_005"),
            new GroupDefinition(23, "01", "8711_006"),
            new GroupDefinition(24, "01", "8711_007"),
            new GroupDefinition(25, "01", "8711_008"),
            new GroupDefinition(26, "01", "8711_009"),
            new GroupDefinition(27, "01", "8711_010"),
            new GroupDefinition(28, "01", "8711_011"),
            new GroupDefinition(29, "01", "8711_012"),
            new GroupDefinition(30, "01", "8711_013"),
            new GroupDefinition(31, "01", "8931_002"),
            new GroupDefinition(32, "01", "9581_001"),
            new GroupDefinition(33, "01", "9581_002"),
            new GroupDefinition(34, "01", "9861_011"),
            new GroupDefinition(35, "01", "9861_013"),
            new GroupDefinition(36, "01", "9861_014"),
            new GroupDefinition(37, "01", "9861_015")
    );

    private final ModbusProperties properties;
    private final RepDataReKpxJejuSukubMRepository jejuOperationRepository;
    private final RepDataHgFcstCurtDaRepository jejuCurtRepository;
    private final RepDataReFcstLfdDaRepository demandRepository;
    private final com.example.megacity_back.service.GenerationMarketSelection marketSelection;

    private final RepDataReFcstGenDaRepository renewableRepository;

    private final AtomicReference<ModbusRegisterMap> snapshotRef = new AtomicReference<>(createEmptySnapshot());
    private final AtomicLong updateCounter = new AtomicLong();

    public ModbusRegisterMap currentSnapshot() {
        return snapshotRef.get();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadInitialSnapshot() {
        refreshSnapshot();
    }

    @Scheduled(fixedDelayString = "${modbus.refresh-interval-ms:60000}", scheduler = "modbusScheduler")
    public synchronized void refreshSnapshot() {
        try {
            ZoneId zoneId = ZoneId.of(properties.getTimeZone());
            long nextCounter = (updateCounter.get() + 1) & ModbusRegisterCodec.UINT32_NULL;

            ModbusRegisterMap.Builder builder = ModbusRegisterMap.builder();
            defineRanges(builder);
            builder.uint32(COMMON_START, nextCounter);

            writeRetiredRanges(builder);
            writeUnavailableActiveRanges(builder);
            writeSection(builder, "operation", section -> writeJejuOperation(section, zoneId));
            writeSection(builder, "curtailment", section -> writeJejuCurtailment(section, zoneId));
            writeSection(builder, "demand", section -> writeDemandForecast(section, zoneId));
            writeSection(builder, "solar", section -> writeRenewableForecast(section, zoneId, "13", SOLAR_FUEL_CODE, RENEWABLE_SOLAR_START));
            writeSection(builder, "wind", section -> writeRenewableForecast(section, zoneId, "16", WIND_FUEL_CODE, RENEWABLE_WIND_START));

            snapshotRef.set(builder.build());
            updateCounter.set(nextCounter);
            log.info("[Modbus] Register snapshot refreshed. updateCounter={}", nextCounter);
        } catch (Exception e) {
            log.error("[Modbus] Failed to refresh register snapshot. Existing snapshot is kept.", e);
        }
    }

    private void defineRanges(ModbusRegisterMap.Builder builder) {
        builder.range(COMMON_START, 100);
        builder.range(HYDROGEN_ACTUAL_START, GROUPS.size() * HYDROGEN_ACTUAL_BLOCK);
        builder.range(HYDROGEN_FORECAST_START, GROUPS.size() * HYDROGEN_FORECAST_BLOCK);
        builder.range(WEATHER_START, GROUPS.size() * WEATHER_BLOCK);
        builder.range(JEJU_OPERATION_START, 20);
        builder.range(JEJU_CURT_START, JEJU_CURT_BLOCK);
        builder.range(DEMAND_START, DEMAND_BLOCK);
        builder.range(RENEWABLE_SOLAR_START, RENEWABLE_BLOCK);
        builder.range(RENEWABLE_WIND_START, RENEWABLE_BLOCK);
    }

    private void writeSection(ModbusRegisterMap.Builder target, String name,
                              java.util.function.Consumer<ModbusRegisterMap.Builder> writer) {
        try {
            var section = ModbusRegisterMap.builder();
            writer.accept(section);
            target.overlay(section.build());
        } catch (Exception exception) {
            log.error("[Modbus] {} unavailable; typed nulls published for this section", name, exception);
        }
    }

    private void writeUnavailableActiveRanges(ModbusRegisterMap.Builder builder) {
        builder.uint32(JEJU_OPERATION_START, null);
        for (int offset = 2; offset < 12; offset += 2) builder.float32(JEJU_OPERATION_START + offset, null);
        builder.uint32(JEJU_CURT_START, null);
        builder.uint32(DEMAND_START, null);
        for (int start : new int[]{RENEWABLE_SOLAR_START, RENEWABLE_WIND_START}) {
            builder.uint32(start, null);
            builder.uint16(start + 2, start == RENEWABLE_SOLAR_START ? SOLAR_FUEL_CODE : WIND_FUEL_CODE);
            builder.uint16(start + 3, null);
        }
        for (int lead = 0; lead <= 48; lead++) {
            int curt = JEJU_CURT_START + 2 + lead * 8;
            builder.uint32(curt, null);
            builder.uint16(curt + 2, null);
            builder.float32(curt + 3, null);
            builder.float32(curt + 5, null);
            int demand = DEMAND_START + 2 + lead * 22;
            builder.uint32(demand, null);
            builder.uint16(demand + 2, null);
            builder.uint16(demand + 3, null);
            for (int offset = 4; offset < 22; offset += 2) builder.float32(demand + offset, null);
            for (int start : new int[]{RENEWABLE_SOLAR_START, RENEWABLE_WIND_START}) {
                int base = start + 4 + lead * 30;
                builder.uint32(base, null);
                builder.uint16(base + 2, null);
                builder.uint16(base + 3, null);
                for (int offset = 4; offset < 30; offset += 2) builder.float32(base + offset, null);
            }
        }
    }

    // Keep the published addresses and typed null encoding for retired blocks.
    private void writeRetiredRanges(ModbusRegisterMap.Builder builder) {
        for (int i = 0; i < GROUPS.size(); i++) {
            int actual = HYDROGEN_ACTUAL_START + i * HYDROGEN_ACTUAL_BLOCK;
            builder.uint32(actual, null);
            builder.float32(actual + 2, null);
            builder.float32(actual + 4, null);
            int hydrogen = HYDROGEN_FORECAST_START + i * HYDROGEN_FORECAST_BLOCK;
            int weather = WEATHER_START + i * WEATHER_BLOCK;
            builder.uint32(hydrogen, null);
            builder.uint32(weather, null);
            for (int lead = 0; lead <= 48; lead++) {
                int h = hydrogen + 2 + lead * 12;
                int w = weather + 2 + lead * 20;
                builder.uint32(h, null);
                builder.uint32(w, null);
                builder.uint16(h + 2, null);
                builder.uint16(h + 3, null);
                builder.uint16(w + 2, null);
                builder.uint16(w + 3, null);
                for (int offset = 4; offset < 8; offset += 2) builder.float32(h + offset, null);
                for (int offset = 4; offset < 20; offset += 2) builder.float32(w + offset, null);
            }
        }
    }

    private void writeJejuOperation(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        RepDataReKpxJejuSukubM record = jejuOperationRepository.findTopByOrderByTmDesc().orElse(null);

        builder.uint32(JEJU_OPERATION_START, epochSeconds(record == null ? null : record.getTm(), zoneId));
        builder.float32(JEJU_OPERATION_START + 2, record == null ? null : record.getCurrPwrTot());
        builder.float32(JEJU_OPERATION_START + 4, record == null ? null : record.getRenewPwrSolar());
        builder.float32(JEJU_OPERATION_START + 6, record == null ? null : record.getRenewPwrWind());
        builder.float32(JEJU_OPERATION_START + 8, record == null ? null : record.getRenewPwrTot());
        builder.float32(JEJU_OPERATION_START + 10, record == null ? null : record.getSuppAbility());
    }

    private void writeJejuCurtailment(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        Optional<RepDataHgFcstCurtDa> latest = jejuCurtRepository.findTopByOrderByCrtnTmDesc();
        List<RepDataHgFcstCurtDa> records = latest
                .map(item -> jejuCurtRepository.findByCrtnTmOrderByFcstTmAsc(item.getCrtnTm()))
                .orElse(List.of());

        builder.uint32(JEJU_CURT_START, latest.map(RepDataHgFcstCurtDa::getCrtnTm)
                .map(value -> epochSeconds(value, zoneId))
                .orElse(null));

        List<RepDataHgFcstCurtDa> timeline = uniqueForecastTimeline(records, RepDataHgFcstCurtDa::getFcstTm);
        for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
            int base = JEJU_CURT_START + 2 + leadIndex * 8;
            RepDataHgFcstCurtDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

            builder.uint32(base, epochSeconds(record == null ? null : record.getFcstTm(), zoneId));
            builder.uint16(base + 2, resolveLeadMinutes(
                    record == null ? null : record.getLeadTm(),
                    record == null ? null : record.getCrtnTm(),
                    record == null ? null : record.getFcstTm(),
                    zoneId
            ));
            builder.float32(base + 3, record == null ? null : record.getFcstCurt());
            builder.float32(base + 5, record == null ? null : record.getFcstMinpw());
        }
    }

    private void writeDemandForecast(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        Optional<RepDataReFcstLfdDa> latest = demandRepository.findTopByOrderByCrtnTmDesc();
        List<RepDataReFcstLfdDa> records = latest
                .map(item -> demandRepository.findByCrtnTmOrderByFcstTmAsc(item.getCrtnTm()))
                .orElse(List.of());

        builder.uint32(DEMAND_START, latest.map(RepDataReFcstLfdDa::getCrtnTm)
                .map(value -> epochSeconds(value, zoneId))
                .orElse(null));

        List<RepDataReFcstLfdDa> timeline = uniqueForecastTimeline(records, RepDataReFcstLfdDa::getFcstTm);
        for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
            int base = DEMAND_START + 2 + leadIndex * 22;
            RepDataReFcstLfdDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

            builder.uint32(base, epochSeconds(record == null ? null : record.getFcstTm(), zoneId));
            builder.uint16(base + 2, resolveLeadMinutes(
                    record == null ? null : record.getLeadTm(),
                    record == null ? null : record.getCrtnTm(),
                    record == null ? null : record.getFcstTm(),
                    zoneId
            ));
            builder.uint16(base + 3, ModbusRegisterCodec.parseUint16(record == null ? null : record.getFcstProdCd()));
            builder.float32(base + 4, record == null ? null : record.getFcstQg01());
            builder.float32(base + 6, record == null ? null : record.getFcstQg02());
            builder.float32(base + 8, record == null ? null : record.getFcstQg03());
            builder.float32(base + 10, record == null ? null : record.getFcstQg04());
            builder.float32(base + 12, record == null ? null : record.getFcstQg05());
            builder.float32(base + 14, record == null ? null : record.getFcstQg06());
            builder.float32(base + 16, record == null ? null : record.getFcstQgen());
            builder.float32(base + 18, record == null ? null : record.getFcstQgmn());
            builder.float32(base + 20, record == null ? null : record.getFcstQgmx());
        }
    }

    private void writeRenewableForecast(ModbusRegisterMap.Builder builder, ZoneId zoneId, String fuelCode, int fuelType, int blockStart) {
        String marketCode = marketSelection.resolve();
        Optional<RepDataReFcstGenDa> latest = renewableRepository.findTopByPwrExcTpCdAndFuelTpCdOrderByCrtnTmDesc(marketCode, fuelCode);
        List<RepDataReFcstGenDa> records = latest
                .map(item -> renewableRepository.findByPwrExcTpCdAndFuelTpCdAndCrtnTmOrderByFcstTmAsc(marketCode, fuelCode, item.getCrtnTm()))
                .orElse(List.of());

        builder.uint32(blockStart, latest.map(RepDataReFcstGenDa::getCrtnTm)
                .map(value -> epochSeconds(value, zoneId))
                .orElse(null));
        builder.uint16(blockStart + 2, fuelType);
        builder.uint16(blockStart + 3, ModbusRegisterCodec.parseUint16(marketCode));

        List<RepDataReFcstGenDa> timeline = uniqueForecastTimeline(records, RepDataReFcstGenDa::getFcstTm);
        for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
            int base = blockStart + 4 + leadIndex * 30;
            RepDataReFcstGenDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

            builder.uint32(base, epochSeconds(record == null ? null : record.getFcstTm(), zoneId));
            builder.uint16(base + 2, resolveLeadMinutes(
                    record == null ? null : record.getLeadTm(),
                    record == null ? null : record.getCrtnTm(),
                    record == null ? null : record.getFcstTm(),
                    zoneId
            ));
            builder.uint16(base + 3, ModbusRegisterCodec.parseUint16(record == null ? null : record.getFcstProdCd()));
            builder.float32(base + 4, record == null ? null : record.getFcstQg01());
            builder.float32(base + 6, record == null ? null : record.getFcstQg02());
            builder.float32(base + 8, record == null ? null : record.getFcstQg03());
            builder.float32(base + 10, record == null ? null : record.getFcstQg04());
            builder.float32(base + 12, record == null ? null : record.getFcstQg05());
            builder.float32(base + 14, record == null ? null : record.getFcstQg06());
            builder.float32(base + 16, record == null ? null : record.getFcstQgen());
            builder.float32(base + 18, record == null ? null : record.getFcstQgmx());
            builder.float32(base + 20, record == null ? null : record.getFcstQgmn());
            builder.float32(base + 22, record == null ? null : record.getFcstCapa());
            builder.float32(base + 24, record == null ? null : record.getEssChrg());
            builder.float32(base + 26, record == null ? null : record.getEssDisc());
            builder.float32(base + 28, record == null ? null : record.getEssCapa());
        }
    }

    private <T> List<T> uniqueForecastTimeline(List<T> records, Function<T, String> keyExtractor) {
        Map<String, T> ordered = new LinkedHashMap<>();
        for (T record : records) {
            String key = keyExtractor.apply(record);
            if (key != null) {
                ordered.putIfAbsent(key, record);
            }
            if (ordered.size() == 49) {
                break;
            }
        }
        return List.copyOf(ordered.values());
    }

    private Integer resolveLeadMinutes(String leadTm, String createdAt, String forecastAt, ZoneId zoneId) {
        Integer parsed = ModbusRegisterCodec.parseLeadMinutes(leadTm);
        if (parsed != null) {
            return parsed;
        }

        Long createdEpoch = epochSeconds(createdAt, zoneId);
        Long forecastEpoch = epochSeconds(forecastAt, zoneId);
        if (createdEpoch == null || forecastEpoch == null) {
            return null;
        }
        return Math.toIntExact(ChronoUnit.MINUTES.between(
                Instant.ofEpochSecond(createdEpoch),
                Instant.ofEpochSecond(forecastEpoch)
        ));
    }

    private Long epochSeconds(String value, ZoneId zoneId) {
        return ModbusRegisterCodec.parseEpochSeconds(value, zoneId);
    }

    private ModbusRegisterMap createEmptySnapshot() {
        ModbusRegisterMap.Builder builder = ModbusRegisterMap.builder();
        defineRanges(builder);
        builder.uint32(COMMON_START, 0L);
        writeRetiredRanges(builder);
        writeUnavailableActiveRanges(builder);
        return builder.build();
    }
}
