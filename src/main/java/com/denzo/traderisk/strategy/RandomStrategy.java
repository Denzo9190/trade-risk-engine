package com.denzo.traderisk.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Component
public class RandomStrategy implements TradingStrategy {

    private final Random random = new Random();

    @Override
    public Optional<Signal> generateSignal(String symbol) {
        if (random.nextBoolean()) {
            return Optional.of(new Signal(
                    symbol,
                    "BUY",
                    BigDecimal.ONE,
                    "RandomStrategy",
                    Instant.now()
            ));
        }
        return Optional.empty();
    }
}
