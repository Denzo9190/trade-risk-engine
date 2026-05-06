package com.denzo.traderisk.marketdata;

import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.marketdata.registry.SymbolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketDataEngineTest {

    @Mock
    private MarketDataAdapter adapter;

    @Mock
    private SymbolRegistry registry;

    private PriceCache priceCache;
    private MarketDataEngine engine;

    @BeforeEach
    void setUp() {
        priceCache = new PriceCache();
        engine = new MarketDataEngine(adapter, priceCache, registry);
    }

    @Test
    void shouldThrowWhenPriceNotInCache() {
        assertThatThrownBy(() -> engine.getPrice("UNKNOWN"))
                .isInstanceOf(PriceNotAvailableException.class)
                .hasMessageContaining("Price not available in cache");
    }

    @Test
    void shouldReturnPriceFromCacheAfterRefresh() {
        Set<String> symbols = Set.of("BTCUSDT", "ETHUSDT");
        when(registry.getAll()).thenReturn(symbols);
        when(adapter.getPrices(symbols)).thenReturn(Map.of(
                "BTCUSDT", new BigDecimal("81500"),
                "ETHUSDT", new BigDecimal("2400")
        ));

        engine.refreshAll();

        assertThat(engine.getPrice("BTCUSDT")).isEqualByComparingTo("81500");
        assertThat(engine.getPrice("ETHUSDT")).isEqualByComparingTo("2400");
    }
}