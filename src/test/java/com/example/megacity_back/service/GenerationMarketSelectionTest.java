package com.example.megacity_back.service;

import com.example.megacity_back.repository.RepDataReFcstGenDaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class GenerationMarketSelectionTest {
    @Test
    void detectsAmbiguityAndPreservesExactConfiguredCode() {
        var repository = mock(RepDataReFcstGenDaRepository.class);
        var selection = new GenerationMarketSelection(repository);
        when(repository.findDistinctMarketCodes()).thenReturn(List.of("9"));
        assertThat(selection.resolve()).isEqualTo("9");
        when(repository.findDistinctMarketCodes()).thenReturn(List.of("01", "09"));
        assertThatThrownBy(selection::resolve).isInstanceOf(IllegalStateException.class);
        ReflectionTestUtils.setField(selection, "configuredCode", "09");
        assertThat(selection.resolve()).isEqualTo("09");
    }
}
