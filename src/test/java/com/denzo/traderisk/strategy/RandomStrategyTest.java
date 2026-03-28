package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.time.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomStrategyTest {

    @Mock
    private MarketDataEngine marketDataEngine;

    @Mock
    private TimeProvider timeProvider;

    @Test
    void shouldGenerateSignalWithPriceFromMarketData() {
        RandomStrategy strategy = new RandomStrategy(marketDataEngine, timeProvider);
        when(marketDataEngine.getPrice(anyString())).thenReturn(new BigDecimal("63500"));

        Instant fixedTime = Instant.parse("2026-03-18T10:00:00Z");
        when(timeProvider.now()).thenReturn(fixedTime);

        Optional<Signal> result = strategy.generateSignal("BTCUSDT");
        assertThat(result).isPresent();
        Signal signal = result.get();
        assertThat(signal.price()).isEqualByComparingTo("63500");
        assertThat(signal.symbol()).isEqualTo("BTCUSDT");
        assertThat(signal.side()).isEqualTo(Side.BUY);
        assertThat(signal.quantity()).isEqualByComparingTo("1");
        assertThat(signal.timestamp()).isEqualTo(fixedTime);
    }
}
