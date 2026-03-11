package com.denzo.traderisk.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMarketPriceService implements MarketPriceService {

    public InMemoryMarketPriceService() {
        prices.put("BTCUSDT", BigDecimal.valueOf(63500));
        prices.put("ETHUSDT", BigDecimal.valueOf(2100));
        prices.put("SOLUSDT", BigDecimal.valueOf(90));
    }

    private final ConcurrentHashMap<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public void updatePrice(String symbol, BigDecimal price) {
        prices.put(symbol, price);
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        BigDecimal price = prices.get(symbol);
        if (price == null) {
            throw new IllegalStateException("No price set for symbol: " + symbol + ". Available symbols: " + prices.keySet());
        }
        return price;
    }
}
