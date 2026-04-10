package com.example.megacity_back.modbus.server;

import com.example.megacity_back.modbus.config.ModbusProperties;
import com.example.megacity_back.modbus.model.ModbusRegisterMap;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class ModbusTcpRequestHandler {

    private static final int PROTOCOL_ID = 0;
    private static final int ILLEGAL_FUNCTION = 0x01;
    private static final int ILLEGAL_DATA_ADDRESS = 0x02;
    private static final int ILLEGAL_DATA_VALUE = 0x03;
    private static final int SERVER_DEVICE_FAILURE = 0x04;

    private final ModbusProperties properties;

    public ModbusTcpRequestHandler(ModbusProperties properties) {
        this.properties = properties;
    }

    public byte[] handle(byte[] requestAdu, ModbusRegisterMap registerMap) {
        if (requestAdu.length < 12) {
            return null;
        }

        int transactionId = u16(requestAdu[0], requestAdu[1]);
        int protocolId = u16(requestAdu[2], requestAdu[3]);
        int declaredLength = u16(requestAdu[4], requestAdu[5]);
        int unitId = requestAdu[6] & 0xFF;

        if (protocolId != PROTOCOL_ID || declaredLength != requestAdu.length - 6) {
            return exceptionResponse(transactionId, unitId, requestAdu[7] & 0xFF, ILLEGAL_DATA_VALUE);
        }

        int functionCode = requestAdu[7] & 0xFF;
        boolean readRegisters = functionCode == 0x04 || (properties.isAllowFunction03Alias() && functionCode == 0x03);
        if (!readRegisters) {
            return exceptionResponse(transactionId, unitId, functionCode, ILLEGAL_FUNCTION);
        }

        int wireStartAddress = u16(requestAdu[8], requestAdu[9]);
        int quantity = u16(requestAdu[10], requestAdu[11]);
        if (quantity < 1 || quantity > ModbusRegisterMap.MAX_READ_QUANTITY) {
            return exceptionResponse(transactionId, unitId, functionCode, ILLEGAL_DATA_VALUE);
        }

        try {
            int[] values = registerMap.readRegisters(wireStartAddress, quantity);
            return successResponse(transactionId, unitId, functionCode, values);
        } catch (IllegalArgumentException illegalAddress) {
            return exceptionResponse(transactionId, unitId, functionCode, ILLEGAL_DATA_ADDRESS);
        } catch (Exception unexpected) {
            return exceptionResponse(transactionId, unitId, functionCode, SERVER_DEVICE_FAILURE);
        }
    }

    private byte[] successResponse(int transactionId, int unitId, int functionCode, int[] values) {
        int byteCount = values.length * 2;
        int pduLength = 2 + byteCount;
        ByteArrayOutputStream out = new ByteArrayOutputStream(7 + pduLength);

        writeHeader(out, transactionId, unitId, pduLength);
        out.write(functionCode);
        out.write(byteCount);
        for (int value : values) {
            out.write((value >>> 8) & 0xFF);
            out.write(value & 0xFF);
        }
        return out.toByteArray();
    }

    private byte[] exceptionResponse(int transactionId, int unitId, int functionCode, int exceptionCode) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(9);
        writeHeader(out, transactionId, unitId, 2);
        out.write(functionCode | 0x80);
        out.write(exceptionCode);
        return out.toByteArray();
    }

    private void writeHeader(ByteArrayOutputStream out, int transactionId, int unitId, int pduLength) {
        out.write((transactionId >>> 8) & 0xFF);
        out.write(transactionId & 0xFF);
        out.write(0);
        out.write(0);
        int length = 1 + pduLength;
        out.write((length >>> 8) & 0xFF);
        out.write(length & 0xFF);
        out.write(unitId & 0xFF);
    }

    private int u16(byte high, byte low) {
        return ((high & 0xFF) << 8) | (low & 0xFF);
    }
}
