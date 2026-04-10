package com.example.megacity_back.modbus;

import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.model.ModbusRegisterMap;
import com.example.megacity_back.modbus.server.ModbusTcpRequestHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModbusTcpRequestHandlerTest {

    @Test
    void readsInputRegistersFromDocumentBaseAddress() {
        ModbusTcpRequestHandler handler = new ModbusTcpRequestHandler(new ModbusProperties());
        ModbusRegisterMap registerMap = baseRegisterMap();

        byte[] response = handler.handle(readRequest(0x1234, 0x04, 0, 2), registerMap);

        assertThat(response).containsExactly(
                0x12, 0x34,
                0x00, 0x00,
                0x00, 0x07,
                0x01,
                0x04,
                0x04,
                0x12, 0x34,
                0x56, 0x78
        );
    }

    @Test
    void translatesLargeDocumentAddressToWireAddress() {
        ModbusTcpRequestHandler handler = new ModbusTcpRequestHandler(new ModbusProperties());
        ModbusRegisterMap registerMap = baseRegisterMap();

        int wireAddress = 90401 - 30001;
        byte[] response = handler.handle(readRequest(0x0001, 0x04, wireAddress, 2), registerMap);

        assertThat(response).containsExactly(
                0x00, 0x01,
                0x00, 0x00,
                0x00, 0x07,
                0x01,
                0x04,
                0x04,
                0x11, 0x11,
                0x22, 0x22
        );
    }

    @Test
    void returnsIllegalDataAddressForUndefinedGap() {
        ModbusTcpRequestHandler handler = new ModbusTcpRequestHandler(new ModbusProperties());
        ModbusRegisterMap registerMap = baseRegisterMap();

        byte[] response = handler.handle(readRequest(0x0002, 0x04, 10, 1), registerMap);

        assertThat(response).containsExactly(
                0x00, 0x02,
                0x00, 0x00,
                0x00, 0x03,
                0x01,
                (byte) 0x84,
                0x02
        );
    }

    private ModbusRegisterMap baseRegisterMap() {
        ModbusRegisterMap.Builder builder = ModbusRegisterMap.builder();
        builder.range(30001, 10);
        builder.range(90401, 20);
        builder.uint16(30001, 0x1234);
        builder.uint16(30002, 0x5678);
        builder.uint16(90401, 0x1111);
        builder.uint16(90402, 0x2222);
        return builder.build();
    }

    private byte[] readRequest(int transactionId, int functionCode, int startAddress, int quantity) {
        return new byte[] {
                (byte) ((transactionId >>> 8) & 0xFF),
                (byte) (transactionId & 0xFF),
                0x00, 0x00,
                0x00, 0x06,
                0x01,
                (byte) functionCode,
                (byte) ((startAddress >>> 8) & 0xFF),
                (byte) (startAddress & 0xFF),
                (byte) ((quantity >>> 8) & 0xFF),
                (byte) (quantity & 0xFF)
        };
    }
}
