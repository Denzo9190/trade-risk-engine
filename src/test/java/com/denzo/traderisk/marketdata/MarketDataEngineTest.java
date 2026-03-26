package com.denzo.traderisk.marketdata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketDataEngineTest {

    @Mock
    private MarketDataAdapter adapter;

    @Mock
    private PriceCache cache;

    @InjectMocks
    private MarketDataEngine engine;

    @Test
    void shouldReturnCachedPriceWhenPresent() {
        when(cache.get("BTCUSDT")).thenReturn(new BigDecimal("63500"));

        BigDecimal price = engine.getPrice("BTCUSDT");

        assertThat(price).isEqualByComparingTo("63500");
        verify(adapter, never()).getPrice(anyString());
    }

    @Test
    void shouldFetchFromAdapterAndCacheOnMiss() {
        when(cache.get("BTCUSDT")).thenReturn(null);
        when(adapter.getPrice("BTCUSDT")).thenReturn(new BigDecimal("63500"));

        BigDecimal price = engine.getPrice("BTCUSDT");

        assertThat(price).isEqualByComparingTo("63500");
        verify(adapter).getPrice("BTCUSDT");
        verify(cache).put("BTCUSDT", new BigDecimal("63500"));
    }
}
