package com.example.megacity_back.modbus.service;

import com.example.megacity_back.entity.RepDataHgFcstGenGentDa;
import com.example.megacity_back.entity.RepDataHgFcstNwpDa;
import com.example.megacity_back.entity.RepDataHgMeasGemGentDa;
import com.example.megacity_back.entity.RepDataP2hFcstCurtDa;
import com.example.megacity_back.entity.RepDataReFcstGenDa;
import com.example.megacity_back.entity.RepDataReFcstLfdDa;
import com.example.megacity_back.entity.RepDataReKpxJejuSukubM;
import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.model.GroupDefinition;
import com.example.megacity_back.modbus.model.ModbusRegisterCodec;
import com.example.megacity_back.modbus.model.ModbusRegisterMap;
import com.example.megacity_back.repository.RepDataHgFcstGenGentDaRepository;
import com.example.megacity_back.repository.RepDataHgFcstNwpDaRepository;
import com.example.megacity_back.repository.RepDataHgMeasGemGentDaRepository;
import com.example.megacity_back.repository.RepDataP2hFcstCurtDaRepository;
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
    private final RepDataHgMeasGemGentDaRepository actualRepository;
    private final RepDataHgFcstGenGentDaRepository hydrogenForecastRepository;
    private final RepDataHgFcstNwpDaRepository weatherForecastRepository;
    private final RepDataReKpxJejuSukubMRepository jejuOperationRepository;
    private final RepDataP2hFcstCurtDaRepository jejuCurtRepository;
    private final RepDataReFcstLfdDaRepository demandRepository;
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

    @Scheduled(fixedDelayString = "${modbus.refresh-interval-ms:60000}")
    public void refreshSnapshot() {
        try {
            ZoneId zoneId = ZoneId.of(properties.getTimeZone());
            long nextCounter = (updateCounter.get() + 1) & ModbusRegisterCodec.UINT32_NULL;

            ModbusRegisterMap.Builder builder = ModbusRegisterMap.builder();
            defineRanges(builder);
            builder.uint32(COMMON_START, nextCounter);

            writeHydrogenActual(builder, zoneId);
            writeHydrogenForecast(builder, zoneId);
            writeWeatherForecast(builder, zoneId);
            writeJejuOperation(builder, zoneId);
            writeJejuCurtailment(builder, zoneId);
            writeDemandForecast(builder, zoneId);
            writeRenewableForecast(builder, zoneId, "13", SOLAR_FUEL_CODE, RENEWABLE_SOLAR_START);
            writeRenewableForecast(builder, zoneId, "16", WIND_FUEL_CODE, RENEWABLE_WIND_START);

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

    private void writeHydrogenActual(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        for (GroupDefinition group : GROUPS) {
            int base = HYDROGEN_ACTUAL_START + (group.groupIndex() - 1) * HYDROGEN_ACTUAL_BLOCK;
            Optional<RepDataHgMeasGemGentDa> record = actualRepository
                    .findFirstByAreaGrpCdAndAreaGrpIdOrderByTmDesc(group.areaGrpCd(), group.areaGrpId());

            if (record.isEmpty()) {
                builder.uint32(base, null);
                builder.float32(base + 2, null);
                builder.float32(base + 4, null);
                continue;
            }

            RepDataHgMeasGemGentDa latest = record.get();
            builder.uint32(base, epochSeconds(latest.getTm(), zoneId));
            builder.float32(base + 2, latest.getHgenCapa());
            builder.float32(base + 4, latest.getHgenProd());
        }
    }

    private void writeHydrogenForecast(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        for (GroupDefinition group : GROUPS) {
            int groupBase = HYDROGEN_FORECAST_START + (group.groupIndex() - 1) * HYDROGEN_FORECAST_BLOCK;
            Optional<RepDataHgFcstGenGentDa> latest = hydrogenForecastRepository
                    .findFirstByAreaGrpCdAndAreaGrpIdOrderByCrtnTmDesc(group.areaGrpCd(), group.areaGrpId());

            List<RepDataHgFcstGenGentDa> records = latest
                    .map(item -> hydrogenForecastRepository.findByAreaGrpCdAndAreaGrpIdAndCrtnTmOrderByFcstTmAsc(
                            group.areaGrpCd(), group.areaGrpId(), item.getCrtnTm()))
                    .orElse(List.of());

            builder.uint32(groupBase, latest.map(RepDataHgFcstGenGentDa::getCrtnTm)
                    .map(value -> epochSeconds(value, zoneId))
                    .orElse(null));

            List<RepDataHgFcstGenGentDa> timeline = uniqueForecastTimeline(records, RepDataHgFcstGenGentDa::getFcstTm);
            for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
                int base = groupBase + 2 + leadIndex * 12;
                RepDataHgFcstGenGentDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

                builder.uint32(base, epochSeconds(record == null ? null : record.getFcstTm(), zoneId));
                builder.uint16(base + 2, resolveLeadMinutes(
                        record == null ? null : record.getLeadTm(),
                        record == null ? null : record.getCrtnTm(),
                        record == null ? null : record.getFcstTm(),
                        zoneId
                ));
                builder.uint16(base + 3, ModbusRegisterCodec.parseUint16(record == null ? null : record.getFcstProdCd()));
                builder.float32(base + 4, record == null ? null : record.getFcstCapa());
                builder.float32(base + 6, record == null ? null : record.getFcstQgen());
            }
        }
    }

    private void writeWeatherForecast(ModbusRegisterMap.Builder builder, ZoneId zoneId) {
        for (GroupDefinition group : GROUPS) {
            int groupBase = WEATHER_START + (group.groupIndex() - 1) * WEATHER_BLOCK;
            Optional<RepDataHgFcstNwpDa> latest = weatherForecastRepository
                    .findFirstByAreaGrpCdAndAreaGrpIdOrderByCrtnTmDesc(group.areaGrpCd(), group.areaGrpId());

            List<RepDataHgFcstNwpDa> records = latest
                    .map(item -> weatherForecastRepository.findByAreaGrpCdAndAreaGrpIdAndCrtnTmOrderByFcstTmAsc(
                            group.areaGrpCd(), group.areaGrpId(), item.getCrtnTm()))
                    .orElse(List.of());

            builder.uint32(groupBase, latest.map(RepDataHgFcstNwpDa::getCrtnTm)
                    .map(value -> epochSeconds(value, zoneId))
                    .orElse(null));

            List<RepDataHgFcstNwpDa> timeline = uniqueForecastTimeline(records, RepDataHgFcstNwpDa::getFcstTm);
            for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
                int base = groupBase + 2 + leadIndex * 20;
                RepDataHgFcstNwpDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

                builder.uint32(base, epochSeconds(record == null ? null : record.getFcstTm(), zoneId));
                builder.uint16(base + 2, resolveLeadMinutes(
                        record == null ? null : record.getLeadTm(),
                        record == null ? null : record.getCrtnTm(),
                        record == null ? null : record.getFcstTm(),
                        zoneId
                ));
                builder.uint16(base + 3, ModbusRegisterCodec.parseUint16(record == null ? null : record.getFcstProdCd()));
                builder.float32(base + 4, record == null ? null : record.getFcstSrad());
                builder.float32(base + 6, record == null ? null : record.getFcstTemp());
                builder.float32(base + 8, record == null ? null : record.getFcstHumi());
                builder.float32(base + 10, record == null ? null : record.getFcstWspd());
                builder.float32(base + 12, record == null ? null : record.getFcstWdir());
                builder.float32(base + 14, record == null ? null : record.getFcstPsfc());
                builder.float32(base + 16, record == null ? null : record.getFcstWsl2());
                builder.float32(base + 18, record == null ? null : record.getFcstWdl2());
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
        Optional<RepDataP2hFcstCurtDa> latest = jejuCurtRepository.findTopByOrderByCrtnTmDesc();
        List<RepDataP2hFcstCurtDa> records = latest
                .map(item -> jejuCurtRepository.findByCrtnTmOrderByFcstTmAsc(item.getCrtnTm()))
                .orElse(List.of());

        builder.uint32(JEJU_CURT_START, latest.map(RepDataP2hFcstCurtDa::getCrtnTm)
                .map(value -> epochSeconds(value, zoneId))
                .orElse(null));

        List<RepDataP2hFcstCurtDa> timeline = uniqueForecastTimeline(records, RepDataP2hFcstCurtDa::getFcstTm);
        for (int leadIndex = 0; leadIndex <= 48; leadIndex++) {
            int base = JEJU_CURT_START + 2 + leadIndex * 8;
            RepDataP2hFcstCurtDa record = leadIndex < timeline.size() ? timeline.get(leadIndex) : null;

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
        Optional<RepDataReFcstGenDa> latest = renewableRepository.findTopByFuelTpCdOrderByCrtnTmDesc(fuelCode);
        List<RepDataReFcstGenDa> records = latest
                .map(item -> renewableRepository.findByFuelTpCdAndCrtnTmOrderByFcstTmAsc(fuelCode, item.getCrtnTm()))
                .orElse(List.of());

        builder.uint32(blockStart, latest.map(RepDataReFcstGenDa::getCrtnTm)
                .map(value -> epochSeconds(value, zoneId))
                .orElse(null));
        builder.uint16(blockStart + 2, fuelType);
        builder.uint16(blockStart + 3, POWER_MARKET_CODE);

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
        return builder.build();
    }
}
