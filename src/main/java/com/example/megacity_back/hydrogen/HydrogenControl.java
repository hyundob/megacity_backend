package com.example.megacity_back.hydrogen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "REP_DATA_HG_CONTROL") @Getter @Setter
public class HydrogenControl {
    @Id @Column(name = "CMD_ID", nullable = false, length = 64) private String commandId;
    @Column(name = "CMD_TYPE", nullable = false, length = 12) private String type;
    @Column(name = "PERCENT_VALUE", precision = 18, scale = 9) private BigDecimal percent;
    @Column(name = "EFFECTIVE_AT", nullable = false) private LocalDateTime effectiveAt;
    @Column(name = "REQUESTED_AT") private LocalDateTime requestedAt;
    @Column(name = "RECEIVED_AT", nullable = false) private LocalDateTime receivedAt;
    @Column(name = "CMD_SEQ", nullable = false, unique = true) private long sequence;
}
