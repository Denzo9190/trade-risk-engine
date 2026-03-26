package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Component
public class RandomStrategy implements TradingStrategy {

    private final Random random;
    private final MarketDataEngine marketDataEngine;  // вместо MarketDataService
    private final TimeProvider timeProvider;

    @Autowired
    public RandomStrategy(MarketDataEngine marketDataEngine, TimeProvider timeProvider) {
        this.marketDataEngine = marketDataEngine;
        this.timeProvider = timeProvider;
        this.random = new Random();
    }

    // конструктор для тестов
    RandomStrategy(MarketDataEngine marketDataEngine, TimeProvider timeProvider, long seed) {
        this.marketDataEngine = marketDataEngine;
        this.timeProvider = timeProvider;
        this.random = new Random(seed);
    }

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        if (random.nextBoolean()) {
            BigDecimal price = marketDataEngine.getPrice(symbol);
            return Optional.of(new Signal(
                    symbol,
                    Side.BUY,
                    BigDecimal.ONE,
                    price,
                    "RandomStrategy",
                    timeProvider.now()
            ));
        }
        return Optional.empty();
    }
}
