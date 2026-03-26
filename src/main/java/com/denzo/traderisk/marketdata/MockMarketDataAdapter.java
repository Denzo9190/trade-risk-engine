package com.denzo.traderisk.marketdata;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Тестовая реализация адаптера с фиксированными ценами.
 * Позволяет обновлять цены через метод updatePrice() для тестов.
 */
@Component
public class MockMarketDataAdapter implements MarketDataAdapter {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public MockMarketDataAdapter() {
        // начальные тестовые цены
        prices.put("BTCUSDT", new BigDecimal("63500"));
        prices.put("ETHUSDT", new BigDecimal("3500"));
        prices.put("AAPL", new BigDecimal("175"));
    }

    /**
     * Обновить цену для символа (для тестов).
     */
    public void updatePrice(String symbol, BigDecimal price) {
        prices.put(symbol, price);
    }

    @Override
    public BigDecimal getPrice(String symbol) {
        BigDecimal price = prices.get(symbol);
        if (price == null) {
            throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
        return price;
    }
}
