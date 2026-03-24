package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.RealisedPnlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PnLEventHandlerTest {

    @Mock
    private RealisedPnlService realisedPnlService;

    @InjectMocks
    private PnLEventHandler handler;

    @Test
    void shouldRecalculatePnLOnTradeEvent() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY, "1");
        handler.handleTradeExecuted(event);
        verify(realisedPnlService).calculateRealisedPnl(event.symbol());
    }
}
