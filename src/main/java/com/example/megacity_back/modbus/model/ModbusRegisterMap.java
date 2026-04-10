package com.example.megacity_back.modbus.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModbusRegisterMap {

    public static final int DOCUMENT_BASE_ADDRESS = 30001;
    public static final int MAX_READ_QUANTITY = 125;

    private final Map<Integer, Integer> registersByDocumentAddress;
    private final List<AddressRange> validRanges;

    private ModbusRegisterMap(Map<Integer, Integer> registersByDocumentAddress, List<AddressRange> validRanges) {
        this.registersByDocumentAddress = Map.copyOf(registersByDocumentAddress);
        this.validRanges = List.copyOf(validRanges);
    }

    public int[] readRegisters(int wireStartAddress, int quantity) {
        if (quantity < 1 || quantity > MAX_READ_QUANTITY) {
            throw new IllegalArgumentException("Quantity out of range");
        }

        int[] values = new int[quantity];
        int documentAddress = toDocumentAddress(wireStartAddress);

        for (int i = 0; i < quantity; i++) {
            int currentAddress = documentAddress + i;
            if (!isValidAddress(currentAddress)) {
                throw new IllegalArgumentException("Illegal data address: " + currentAddress);
            }
            values[i] = registersByDocumentAddress.getOrDefault(currentAddress, 0);
        }
        return values;
    }

    public static int toDocumentAddress(int wireStartAddress) {
        return DOCUMENT_BASE_ADDRESS + wireStartAddress;
    }

    private boolean isValidAddress(int documentAddress) {
        for (AddressRange range : validRanges) {
            if (range.contains(documentAddress)) {
                return true;
            }
        }
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<Integer, Integer> registers = new HashMap<>();
        private final List<AddressRange> validRanges = new ArrayList<>();

        public Builder range(int documentStartAddress, int length) {
            validRanges.add(new AddressRange(documentStartAddress, documentStartAddress + length - 1));
            return this;
        }

        public Builder raw(int documentAddress, int value) {
            registers.put(documentAddress, value & 0xFFFF);
            return this;
        }

        public Builder uint16(int documentAddress, Integer value) {
            registers.put(documentAddress, ModbusRegisterCodec.encodeUint16(value));
            return this;
        }

        public Builder uint32(int documentAddress, Long value) {
            int[] encoded = ModbusRegisterCodec.encodeUint32(value);
            registers.put(documentAddress, encoded[0]);
            registers.put(documentAddress + 1, encoded[1]);
            return this;
        }

        public Builder float32(int documentAddress, BigDecimal value) {
            int[] encoded = ModbusRegisterCodec.encodeFloat32(value);
            registers.put(documentAddress, encoded[0]);
            registers.put(documentAddress + 1, encoded[1]);
            return this;
        }

        public ModbusRegisterMap build() {
            return new ModbusRegisterMap(registers, validRanges);
        }
    }

    private record AddressRange(int start, int end) {
        private boolean contains(int value) {
            return value >= start && value <= end;
        }
    }
}
