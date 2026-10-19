package com.example.megacity_back.hydrogen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "REP_DATA_HG_HOURLY") @Getter @Setter
public class HydrogenHourly {
    @Id @Column(name = "TM", nullable = false, length = 12) private String tm;
    @Column(name = "INTERVAL_START", nullable = false) private LocalDateTime intervalStart;
    @Column(name = "INTERVAL_END", nullable = false) private LocalDateTime intervalEnd;
    @Column(name = "DURATION_SECONDS", nullable = false) private int durationSeconds;
    @Column(name = "NORMAL_PROD_KG", nullable = false, precision = 18, scale = 9) private BigDecimal normalProductionKg;
    @Column(name = "DR_PROD_KG", nullable = false, precision = 18, scale = 9) private BigDecimal drProductionKg;
    @Column(name = "REDUCED_PROD_KG", nullable = false, precision = 18, scale = 9) private BigDecimal reducedProductionKg;
    @Column(name = "PROD_KG", nullable = false, precision = 18, scale = 9) private BigDecimal productionKg;
    @Column(name = "ENERGY_KWH", nullable = false, precision = 18, scale = 9) private BigDecimal energyKwh;
    @Column(name = "OPEN_BG_NM3", nullable = false, precision = 18, scale = 9) private BigDecimal openingBiogasNm3;
    @Column(name = "REFILLED_BG_NM3", nullable = false, precision = 18, scale = 9) private BigDecimal refilledBiogasNm3;
    @Column(name = "USED_BG_NM3", nullable = false, precision = 18, scale = 9) private BigDecimal usedBiogasNm3;
    @Column(name = "STORED_BG_NM3", nullable = false, precision = 18, scale = 9) private BigDecimal storedBiogasNm3;
    @Column(name = "OPEN_HG_KG", nullable = false, precision = 18, scale = 9) private BigDecimal openingHydrogenKg;
    @Column(name = "STORED_HG_KG", nullable = false, precision = 18, scale = 9) private BigDecimal storedHydrogenKg;
    @Column(name = "SHIPPED_HG_KG", nullable = false, precision = 18, scale = 9) private BigDecimal shippedHydrogenKg;
    @Column(name = "TANK_EXCHANGES", nullable = false) private int tankExchanges;
    @Column(name = "DR_SECONDS", nullable = false) private int drSeconds;
    @Column(name = "STOP_SECONDS", nullable = false) private int stopSeconds;
    @Column(name = "REDUCED_SECONDS", nullable = false) private int reducedSeconds;
    @Column(name = "REG_DATE", nullable = false) private LocalDateTime regDate;
}
