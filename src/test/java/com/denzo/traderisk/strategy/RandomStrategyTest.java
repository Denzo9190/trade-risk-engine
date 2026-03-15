package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RandomStrategyTest {

    private final RandomStrategy strategy = new RandomStrategy(42); // детерминированный seed

    @Test
    void shouldGenerateDeterministicSignal() {
        Optional<Signal> result = strategy.generateSignal("BTCUSDT");
        assertThat(result).isPresent();
        Signal signal = result.get();
        assertThat(signal.symbol()).isEqualTo("BTCUSDT");
        assertThat(signal.side()).isEqualTo(Side.BUY);
        assertThat(signal.quantity()).isEqualByComparingTo("1");
        assertThat(signal.price()).isNotNull();
        assertThat(signal.price()).isGreaterThan(BigDecimal.ZERO);
        assertThat(signal.strategyName()).isEqualTo("RandomStrategy");
        assertThat(signal.timestamp()).isNotNull();
    }
}
