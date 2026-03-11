package com.denzo.traderisk.cache;

import com.denzo.traderisk.dto.PositionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PositionCacheTest {

    private PositionCache positionCache;

    @BeforeEach
    void setUp() {
        positionCache = new PositionCache();
    }

    @Test
    void shouldStoreAndRetrievePosition() {
        PositionResponse position = new PositionResponse(
                "BTCUSDT", BigDecimal.valueOf(1.5), BigDecimal.valueOf(60333), BigDecimal.valueOf(4750)
        );
        positionCache.put("BTCUSDT", position);
        assertThat(positionCache.get("BTCUSDT")).isEqualTo(position);
    }

    @Test
    void shouldRemovePosition() {
        PositionResponse position = new PositionResponse(
                "BTCUSDT", BigDecimal.valueOf(1.5), BigDecimal.valueOf(60333), BigDecimal.valueOf(4750)
        );
        positionCache.put("BTCUSDT", position);
        positionCache.remove("BTCUSDT");
        assertThat(positionCache.get("BTCUSDT")).isNull();
    }

    @Test
    void shouldClearCache() {
        positionCache.put("BTCUSDT", new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        positionCache.put("ETHUSDT", new PositionResponse("ETHUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(3000), BigDecimal.ZERO));
        positionCache.clear();
        assertThat(positionCache.get("BTCUSDT")).isNull();
        assertThat(positionCache.get("ETHUSDT")).isNull();
    }
}