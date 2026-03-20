package com.denzo.traderisk.market.historical;

import com.denzo.traderisk.market.MarketDataNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory хранилище исторических цен.
 * Позволяет добавлять цены через addPrice (для тестов).
 */
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
            throw new MarketDataNotFoundException(symbol);
        }
        Map.Entry<Instant, BigDecimal> entry = series.floorEntry(timestamp);
        if (entry == null) {
            throw new MarketDataNotFoundException(symbol + " at " + timestamp);
        }
        System.out.println(">>> Historical: found price " + entry.getValue() + " for " + symbol + " at " + timestamp);
        return entry.getValue();
    }
}
