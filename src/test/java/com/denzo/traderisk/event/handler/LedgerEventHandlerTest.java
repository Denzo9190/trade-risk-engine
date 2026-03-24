package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.LedgerService;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LedgerEventHandlerTest {

    @Mock
    private LedgerService ledgerService;

    @Mock
    private PositionService positionService;

    @Mock
    private RealisedPnlService realisedPnlService;

    @InjectMocks
    private LedgerEventHandler handler;

    @Test
    void shouldRecordLedgerOnTradeEvent() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY, "1");
        PositionResponse position = new PositionResponse("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), BigDecimal.ZERO);
        RealisedPnlResponse realised = new RealisedPnlResponse("BTCUSDT", BigDecimal.ZERO);

        when(positionService.getPosition(event.symbol())).thenReturn(position);
        when(realisedPnlService.calculateRealisedPnl(event.symbol())).thenReturn(realised);

        handler.handleTradeExecuted(event);

        verify(ledgerService).recordTrade(event, position, realised.realisedPnl());
    }
}
