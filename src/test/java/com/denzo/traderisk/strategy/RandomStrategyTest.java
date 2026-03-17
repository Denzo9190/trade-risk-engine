package com.denzo.traderisk.strategy;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.market.MarketDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RandomStrategyTest {

    @Mock
    private MarketDataService marketDataService;

    @Test
    void shouldGenerateSignalWithPriceFromMarketData() {
        RandomStrategy strategy = new RandomStrategy(marketDataService, 42L);
        when(marketDataService.getPrice(anyString())).thenReturn(new BigDecimal("63500"));

        Optional<Signal> result = strategy.generateSignal("BTCUSDT");
        assertThat(result).isPresent();
        Signal signal = result.get();
        assertThat(signal.price()).isEqualByComparingTo("63500");
        assertThat(signal.symbol()).isEqualTo("BTCUSDT");
        assertThat(signal.side()).isEqualTo(Side.BUY);
        assertThat(signal.quantity()).isEqualByComparingTo("1");
    }
}
