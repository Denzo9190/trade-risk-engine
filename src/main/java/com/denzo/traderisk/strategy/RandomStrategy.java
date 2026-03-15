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

    public RandomStrategy() {
        this.random = new Random();
    }

    // конструктор для тестов
    RandomStrategy(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        if (random.nextBoolean()) {
            // генерируем случайную цену в диапазоне 50000–70000
            BigDecimal price = BigDecimal.valueOf(50000 + random.nextInt(20001));
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
