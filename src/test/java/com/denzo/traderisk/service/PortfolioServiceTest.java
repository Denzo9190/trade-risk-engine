package com.denzo.traderisk.service;

import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.repository.TradeRepository;
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
    private TradeRepository tradeRepository;

    @Mock
    private PositionService positionService;

    @Mock
    private RealisedPnlService realisedPnlService;

    @Mock
    private MarketPriceService marketPriceService;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void shouldAggregatePortfolio() {
        // given
        List<String> symbols = List.of("BTCUSDT", "ETHUSDT");
        when(tradeRepository.findDistinctSymbols()).thenReturn(symbols);

        // мок для BTCUSDT
        PositionResponse btcPosition = new PositionResponse(
                "BTCUSDT",
                BigDecimal.valueOf(1.5),
                new BigDecimal("60333.33333333"),
                new BigDecimal("4750.00000001")
        );
        when(positionService.getPosition("BTCUSDT")).thenReturn(btcPosition);
        when(realisedPnlService.calculateRealisedPnl("BTCUSDT"))
                .thenReturn(new RealisedPnlResponse("BTCUSDT", new BigDecimal("4000.00000000")));

        // мок для ETHUSDT
        PositionResponse ethPosition = new PositionResponse(
                "ETHUSDT",
                BigDecimal.valueOf(-2),
                new BigDecimal("3000.00000000"),
                new BigDecimal("200.00000000")
        );
        when(positionService.getPosition("ETHUSDT")).thenReturn(ethPosition);
        when(realisedPnlService.calculateRealisedPnl("ETHUSDT"))
                .thenReturn(new RealisedPnlResponse("ETHUSDT", new BigDecimal("500.00000000")));

        // when
        PortfolioResponse response = portfolioService.getPortfolio();

        // then
        // totalRealised = 4000 + 500 = 4500
        assertThat(response.totalRealisedPnl()).isEqualByComparingTo("4500");

        // totalUnrealised = 4750.00000001 + 200 = 4950.00000001
        BigDecimal expectedUnrealised = new BigDecimal("4950.00000001");
        assertThat(response.totalUnrealisedPnl()).isEqualByComparingTo(expectedUnrealised);

        // totalExposure = 1.5 * 60333.33333333 + 2 * 3000 = 90499.999999995 + 6000 = 96499.999999995
        // из-за округления в FinancialMath может получиться 96500.00000000, поэтому используем compareTo
        assertThat(response.totalExposure()).isEqualByComparingTo(new BigDecimal("96499.999999995"));
    }
}
