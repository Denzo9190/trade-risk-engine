package com.denzo.traderisk.strategy;

import com.denzo.traderisk.marketdata.MarketDataEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomStrategyTest {

    @Mock
    private MarketDataEngine marketDataEngine;
    @InjectMocks
    private RandomStrategy strategy;

    @Test
    void shouldGenerateSignal() {
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(new BigDecimal("63500"));
        Optional<TradingSignal> result = strategy.generateSignal("BTCUSDT");
        assertThat(result).isPresent();
        TradingSignal signal = result.get();
        assertThat(signal.symbol()).isEqualTo("BTCUSDT");
        assertThat(signal.type()).isEqualTo(SignalType.BUY);
        assertThat(signal.price()).isEqualByComparingTo("63500");
        assertThat(signal.quantity()).isEqualByComparingTo("1");
    }
}
