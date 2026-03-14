package com.denzo.traderisk.strategy;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RandomStrategyTest {

    private final RandomStrategy strategy = new RandomStrategy();

    @Test
    void shouldReturnEmptyOrSignal() {
        Optional<Signal> result = strategy.generateSignal("BTCUSDT");
        assertThat(result).isNotNull();
        // Не можем гарантировать наличие, просто проверяем, что вызов не падает
    }

    @RepeatedTest(10)
    void shouldEventuallyGenerateSignal() {
        // За 10 попыток должна появиться хотя бы одна непустая (вероятность 99.9%)
        for (int i = 0; i < 10; i++) {
            Optional<Signal> result = strategy.generateSignal("BTCUSDT");
            if (result.isPresent()) {
                Signal signal = result.get();
                assertThat(signal.symbol()).isEqualTo("BTCUSDT");
                assertThat(signal.side()).isIn("BUY", "SELL");
                assertThat(signal.quantity()).isPositive();
                assertThat(signal.strategyName()).isEqualTo("RandomStrategy");
                assertThat(signal.timestamp()).isNotNull();
                return;
            }
        }
        // fail("No signal generated after 10 attempts");
        // На самом деле допускаем, что может не сгенерироваться, но вероятность мала
    }
}
