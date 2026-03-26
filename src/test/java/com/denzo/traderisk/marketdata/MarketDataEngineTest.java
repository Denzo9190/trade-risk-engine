package com.denzo.traderisk.marketdata;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarketDataEngineTest {

    @Test
    void shouldReturnPriceFromMockAdapter() {
        MarketDataAdapter adapter = new MockMarketDataAdapter();
        MarketDataEngine engine = new MarketDataEngine(adapter);

        BigDecimal price = engine.getPrice("BTCUSDT");
        assertThat(price).isEqualByComparingTo("63500");
    }

    @Test
    void shouldThrowForUnknownSymbol() {
        MarketDataAdapter adapter = new MockMarketDataAdapter();
        MarketDataEngine engine = new MarketDataEngine(adapter);

        assertThatThrownBy(() -> engine.getPrice("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown symbol");
    }
}
