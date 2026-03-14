package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RandomStrategyTest {

    private final RandomStrategy strategy = new RandomStrategy(42); // фиксированный seed

    @Test
    void shouldGenerateDeterministicSignal() {
        Optional<Signal> result = strategy.generateSignal("BTCUSDT");
        // При seed=42 первый вызов должен вернуть сигнал (можно вычислить заранее)
        assertThat(result).isPresent();
        Signal signal = result.get();
        assertThat(signal.symbol()).isEqualTo("BTCUSDT");
        assertThat(signal.side()).isEqualTo(Side.BUY);
        assertThat(signal.quantity()).isEqualByComparingTo("1");
        assertThat(signal.strategyName()).isEqualTo("RandomStrategy");
        assertThat(signal.timestamp()).isNotNull();
    }
}
