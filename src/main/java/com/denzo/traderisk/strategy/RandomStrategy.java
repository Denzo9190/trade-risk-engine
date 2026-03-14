package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Component
public class RandomStrategy implements TradingStrategy {

    private final Random random;

    // Конструктор без параметров для продакшена
    public RandomStrategy() {
        this.random = new Random();
    }

    // Конструктор с seed для тестов (можно оставить package-private)
    RandomStrategy(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        if (random.nextBoolean()) {
            return Optional.of(new Signal(
                    symbol,
                    Side.BUY,
                    BigDecimal.ONE,
                    "RandomStrategy",
                    Instant.now()
            ));
        }
        return Optional.empty();
    }
}
