package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.market.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Component
public class RandomStrategy implements TradingStrategy {

    private final Random random;
    private final MarketDataService marketDataService;

    @Autowired
    public RandomStrategy(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
        this.random = new Random();
    }

    // конструктор для тестов
    RandomStrategy(MarketDataService marketDataService, long seed) {
        this.marketDataService = marketDataService;
        this.random = new Random(seed);
    }

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        if (random.nextBoolean()) {
            BigDecimal price = marketDataService.getPrice(symbol);
            return Optional.of(new Signal(
                    symbol,
                    Side.BUY,
                    BigDecimal.ONE,
                    price,
                    "RandomStrategy",
                    Instant.now()
            ));
        }
        return Optional.empty();
    }
}
