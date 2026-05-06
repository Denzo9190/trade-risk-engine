package com.denzo.traderisk.marketdata;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PriceCache {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public BigDecimal get(String symbol) {
        return prices.get(symbol);
    }

    public void put(String symbol, BigDecimal price) {
        prices.put(symbol, price);
    }

    public void putAll(Map<String, BigDecimal> priceMap) {
        prices.putAll(priceMap);
    }

    public Set<String> getAllSymbols() {
        return prices.keySet();
    }

    public void clear() {
        prices.clear();
    }
}
