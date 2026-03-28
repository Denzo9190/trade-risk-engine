package com.denzo.traderisk.marketdata.adapter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!backtest")
@Primary
public class MockMarketDataAdapter implements MarketDataAdapter {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public MockMarketDataAdapter() {
        // начальные значения, но они будут перезаписываться
        prices.put("BTCUSDT", new BigDecimal("63500"));
        prices.put("ETHUSDT", new BigDecimal("3500"));
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        if ("BTCUSDT".equals(symbol)) {
            // генерируем случайную цену в диапазоне 63000–64000
            BigDecimal price = BigDecimal.valueOf(63000 + random.nextInt(1001));
            // сохраняем в кэш (хотя он не используется для ответа, но для единообразия)
            prices.put(symbol, price);
            return price;
        }
        BigDecimal price = prices.get(symbol);
        if (price == null) {
            throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
        return price;
    }
}
