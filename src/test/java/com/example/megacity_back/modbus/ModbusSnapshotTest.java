package com.example.megacity_back.modbus;

import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.model.ModbusRegisterMap;
import com.example.megacity_back.modbus.service.ModbusRegisterSnapshotService;
import com.example.megacity_back.repository.*;
import com.example.megacity_back.service.GenerationMarketSelection;
import com.example.megacity_back.entity.RepDataReKpxJejuSukubM;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModbusSnapshotTest {
    @Test
    void retiredAddressesStayReadableAndFailedSectionDoesNotBlockOperation() {
        var operation = mock(RepDataReKpxJejuSukubMRepository.class);
        var curt = mock(RepDataHgFcstCurtDaRepository.class);
        var demand = mock(RepDataReFcstLfdDaRepository.class);
        var generation = mock(RepDataReFcstGenDaRepository.class);
        var market = mock(GenerationMarketSelection.class);
        when(market.resolve()).thenThrow(new IllegalStateException("No generation data"));
        when(curt.findTopByOrderByCrtnTmDesc()).thenThrow(new IllegalStateException("Unavailable"));
        var row = new RepDataReKpxJejuSukubM();
        row.setTm("202609081200");
        row.setSuppAbility(BigDecimal.valueOf(1000));
        when(operation.findTopByOrderByTmDesc()).thenReturn(Optional.of(row));
        var service = new ModbusRegisterSnapshotService(new ModbusProperties(), operation, curt, demand, market, generation);
        assertNullFloat(service.currentSnapshot(), 30103);
        service.refreshSnapshot();
        var snapshot = service.currentSnapshot();
        assertThat(snapshot.readRegisters(0, 2)).containsExactly(0, 1);
        assertThat(snapshot.readRegisters(30101 - 30001, 2)).containsExactly(65535, 65535);
        assertNullFloat(snapshot, 30507);
        assertNullFloat(snapshot, 53007);
        assertNullFloat(snapshot, 90426);
        assertNullFloat(snapshot, 91929);
        assertThat(readFloat(snapshot, 90411)).isEqualTo(1000f);
        assertThat(snapshot.readRegisters(90413 - 30001, 2)).containsExactly(0, 0);
    }

    private static float readFloat(ModbusRegisterMap map, int address) {
        int[] words = map.readRegisters(address - 30001, 2);
        return Float.intBitsToFloat((words[0] << 16) | words[1]);
    }
    private static void assertNullFloat(ModbusRegisterMap map, int address) {
        assertThat(Float.isNaN(readFloat(map, address))).isTrue();
    }
}
