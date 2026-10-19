package com.example.megacity_back.hydrogen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "REP_DATA_HG_SYS_STATE") @Getter @Setter
public class HydrogenState {
    @Id @Column(name = "SYS_ID", nullable = false, length = 12) private String systemId;
    @Column(name = "STARTED_AT", nullable = false) private LocalDateTime startedAt;
    @Column(name = "CURSOR_AT", nullable = false) private LocalDateTime cursorAt;
    @Column(name = "STORED_BG_NM3", nullable = false, precision = 18, scale = 9) private BigDecimal storedBiogasNm3;
    @Column(name = "STORED_HG_KG", nullable = false, precision = 18, scale = 9) private BigDecimal storedHydrogenKg;
    @Column(name = "DAILY_HG_KG", nullable = false, precision = 18, scale = 9) private BigDecimal dailyHydrogenKg;
    @Column(name = "BG_PER_KG", nullable = false, precision = 18, scale = 9) private BigDecimal gasPerKg;
    @Column(name = "ENERGY_PER_KG", nullable = false, precision = 18, scale = 9) private BigDecimal energyPerKg;
    @Column(name = "MAX_PROD_KG_H", nullable = false, precision = 18, scale = 9) private BigDecimal maxProductionKgH;
    @Column(name = "REFILL_THRESHOLD", nullable = false, precision = 18, scale = 9) private BigDecimal refillThresholdNm3;
    @Column(name = "REFILL_TARGET", nullable = false, precision = 18, scale = 9) private BigDecimal refillTargetNm3;
    @Column(name = "TANK_CAPA_KG", nullable = false, precision = 18, scale = 9) private BigDecimal tankCapacityKg;
    @Column(name = "REG_DATE", nullable = false) private LocalDateTime regDate;
    @Column(name = "UPD_DATE", nullable = false) private LocalDateTime updDate;
}
