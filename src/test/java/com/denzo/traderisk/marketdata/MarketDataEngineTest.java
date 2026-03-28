package com.denzo.traderisk.marketdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketDataEngineTest {

    @Mock
    private PriceCache cache;

    @InjectMocks
    private MarketDataEngine engine;

    @Test
    void shouldReturnPriceFromCache() {
        when(cache.get("BTCUSDT")).thenReturn(new BigDecimal("63500"));

        BigDecimal price = engine.getPrice("BTCUSDT");

        assertThat(price).isEqualByComparingTo("63500");
    }

    @Test
    void shouldThrowWhenPriceNotAvailable() {
        when(cache.get("BTCUSDT")).thenReturn(null);

        assertThatThrownBy(() -> engine.getPrice("BTCUSDT"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Price not available");
    }
}
