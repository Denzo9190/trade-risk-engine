package com.denzo.traderisk.marketdata.adapter;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("!backtest")
@Primary
public class MockMarketDataAdapter implements MarketDataAdapter {

    private final Random random = new Random();

    @Override
    public BigDecimal getPrice(String symbol) {
        return generate(symbol);
    }

    @Override
    public Map<String, BigDecimal> getPrices(Set<String> symbols) {
        return symbols.stream()
                .collect(Collectors.toMap(s -> s, this::generate));
    }

    private BigDecimal generate(String symbol) {
        int min, max;
        switch (symbol) {
            case "BTCUSDT" -> { min = 60000; max = 85000; }
            case "ETHUSDT" -> { min = 2200; max = 2800; }
            case "SOLUSDT" -> { min = 80; max = 110; }
            default -> throw new IllegalArgumentException("Unsupported symbol: " + symbol);
        }
        double price = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}
