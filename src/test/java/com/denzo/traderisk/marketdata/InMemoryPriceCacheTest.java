package com.denzo.traderisk.marketdata;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPriceCacheTest {

    private final InMemoryPriceCache cache = new InMemoryPriceCache();

    @Test
    void shouldStoreAndRetrievePrice() {
        cache.put("BTCUSDT", new BigDecimal("63500"));
        BigDecimal price = cache.get("BTCUSDT");
        assertThat(price).isEqualByComparingTo("63500");
    }

    @Test
    void shouldReturnNullForUnknownSymbol() {
        assertThat(cache.get("UNKNOWN")).isNull();
    }

    @Test
    void shouldOverwriteExistingPrice() {
        cache.put("BTCUSDT", new BigDecimal("60000"));
        cache.put("BTCUSDT", new BigDecimal("63500"));
        assertThat(cache.get("BTCUSDT")).isEqualByComparingTo("63500");
    }

    @Test
    void shouldClearCache() {
        cache.put("BTCUSDT", new BigDecimal("63500"));
        cache.clear();
        assertThat(cache.get("BTCUSDT")).isNull();
    }
}
