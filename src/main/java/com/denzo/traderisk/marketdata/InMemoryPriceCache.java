package com.denzo.traderisk.marketdata;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPriceCache implements PriceCache {

    private static class CachedPrice {
        final BigDecimal price;
        final Instant timestamp;

        CachedPrice(BigDecimal price, Instant timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    private final Map<String, CachedPrice> cache = new ConcurrentHashMap<>();

    @Override
    public BigDecimal get(String symbol) {
        CachedPrice cached = cache.get(symbol);
        return cached != null ? cached.price : null;
    }

    @Override
    public void put(String symbol, BigDecimal price) {
        cache.put(symbol, new CachedPrice(price, Instant.now()));
    }

    public void clear() {
        cache.clear();
    }
}
