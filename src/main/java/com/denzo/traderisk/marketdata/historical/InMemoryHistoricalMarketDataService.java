package com.denzo.traderisk.marketdata.historical;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryHistoricalMarketDataService implements HistoricalMarketDataService {

    private final Map<String, NavigableMap<Instant, BigDecimal>> data = new ConcurrentHashMap<>();

    public void addPrice(String symbol, Instant time, BigDecimal price) {
        data.computeIfAbsent(symbol, k -> new TreeMap<>()).put(time, price);
    }

    @Override
    public BigDecimal getPrice(String symbol, Instant timestamp) {
        NavigableMap<Instant, BigDecimal> series = data.get(symbol);
        if (series == null) {
            throw new HistoricalDataNotFoundException(symbol, timestamp);
        }
        Map.Entry<Instant, BigDecimal> entry = series.floorEntry(timestamp);
        if (entry == null) {
            throw new HistoricalDataNotFoundException(symbol, timestamp);
        }
        return entry.getValue();
    }
}
