package com.denzo.traderisk.bootstrap;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PositionBootstrapTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionService positionService;

    @Autowired
    private RealisedPnlService realisedPnlService;

    @Autowired
    private PositionBootstrap positionBootstrap;

    @Test
    @Sql(scripts = "/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRebuildPositionsFromTrades() {
        Trade trade1 = new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("60000"), Side.BUY, "order1");
        Trade trade2 = new Trade("BTCUSDT", BigDecimal.ONE, new BigDecimal("61000"), Side.SELL, "order2");
        tradeRepository.save(trade1);
        tradeRepository.save(trade2);

        positionBootstrap.rebuildPositionsFromTrades();

        PositionResponse position = positionService.getPosition("BTCUSDT");
        assertThat(position.totalQuantity()).isEqualByComparingTo("0");

        BigDecimal realised = realisedPnlService.calculateRealisedPnl("BTCUSDT").realisedPnl();
        assertThat(realised).isEqualByComparingTo("1000");
    }
}
