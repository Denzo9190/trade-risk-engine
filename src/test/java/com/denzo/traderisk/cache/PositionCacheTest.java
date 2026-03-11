package com.denzo.traderisk.cache;

import com.denzo.traderisk.dto.PositionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class PositionCacheTest {

    private final PositionCache positionCache = new PositionCache();

    @Test
    void computeIfAbsent_shouldComputeAndCache() {
        String symbol = "BTCUSDT";
        PositionResponse expected = new PositionResponse(symbol, BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO);

        Function<String, PositionResponse> computer = s -> expected;

        PositionResponse result = positionCache.computeIfAbsent(symbol, computer);

        assertThat(result).isEqualTo(expected);
        // второй вызов должен вернуть тот же объект (из кэша)
        PositionResponse cached = positionCache.computeIfAbsent(symbol, computer);
        assertThat(cached).isSameAs(expected);
    }

    @Test
    void remove_shouldEvict() {
        String symbol = "BTCUSDT";
        PositionResponse pos = new PositionResponse(symbol, BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO);
        positionCache.computeIfAbsent(symbol, s -> pos);

        positionCache.remove(symbol);
        // после удаления computeIfAbsent должен вызвать computer заново
        PositionResponse newPos = new PositionResponse(symbol, BigDecimal.valueOf(2), BigDecimal.valueOf(61000), BigDecimal.ZERO);
        PositionResponse result = positionCache.computeIfAbsent(symbol, s -> newPos);

        assertThat(result).isEqualTo(newPos);
    }

    @Test
    void clear_shouldEmptyCache() {
        positionCache.computeIfAbsent("BTCUSDT", s -> new PositionResponse("BTCUSDT", BigDecimal.ONE, BigDecimal.valueOf(60000), BigDecimal.ZERO));
        positionCache.clear();
        // после очистки вызов должен привести к вычислению заново
        PositionResponse pos = positionCache.computeIfAbsent("BTCUSDT", s -> new PositionResponse("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(61000), BigDecimal.ZERO));
        assertThat(pos.totalQuantity()).isEqualByComparingTo(BigDecimal.valueOf(2));
    }
}
