package com.denzo.traderisk.cache;

import com.denzo.traderisk.dto.PositionResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PositionCache {

    private final Map<String, PositionResponse> cache = new ConcurrentHashMap<>();

    public PositionResponse get(String symbol) {
        return cache.get(symbol);
    }

    public void put(String symbol, PositionResponse position) {
        cache.put(symbol, position);
    }

    public void remove(String symbol) {
        cache.remove(symbol);
    }

    public void clear() {
        cache.clear();
    }
}
