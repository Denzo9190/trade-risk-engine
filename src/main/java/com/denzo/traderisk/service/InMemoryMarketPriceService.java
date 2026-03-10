package com.denzo.traderisk.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryMarketPriceService implements MarketPriceService {

    private final ConcurrentHashMap<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public void updatePrice(String symbol, BigDecimal price) {
        prices.put(symbol, price);
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        return prices.get(symbol);
    }
}
