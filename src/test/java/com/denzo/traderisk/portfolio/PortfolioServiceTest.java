package com.denzo.traderisk.portfolio;

import com.denzo.traderisk.domain.Position;
import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import com.denzo.traderisk.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private MarketDataEngine marketDataEngine;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void shouldAggregatePortfolio() {
        Position btc = new Position("BTCUSDT");
        btc.applyTrade(BigDecimal.ONE, new BigDecimal("60000"), Side.BUY);

        Position eth = new Position("ETHUSDT");
        eth.applyTrade(BigDecimal.ONE, new BigDecimal("3000"), Side.BUY);

        when(positionRepository.findAll()).thenReturn(List.of(btc, eth));
        when(marketDataEngine.getPrice("BTCUSDT")).thenReturn(new BigDecimal("65000"));
        when(marketDataEngine.getPrice("ETHUSDT")).thenReturn(new BigDecimal("3500"));

        PortfolioSnapshot snapshot = portfolioService.getPortfolio();

        assertThat(snapshot.totalUnrealisedPnl()).isEqualByComparingTo("5500"); // 5000 + 500
        assertThat(snapshot.totalRealisedPnl()).isEqualByComparingTo("0");
        assertThat(snapshot.totalEquity()).isEqualByComparingTo("5500");
        assertThat(snapshot.positions()).hasSize(2);
        assertThat(snapshot.totalExposure()).isEqualByComparingTo("63000"); // 1*60000 + 1*3000
    }
}
