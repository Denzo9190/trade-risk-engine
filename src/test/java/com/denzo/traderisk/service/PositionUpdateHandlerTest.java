package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.TradeExecutedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PositionUpdateHandlerTest {

    @Mock
    private PositionService positionService;

    @Mock
    private LedgerService ledgerService;

    @InjectMocks
    private PositionUpdateHandler handler;

    @Test
    void shouldUpdatePositionAndLedgerOnTrade() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.ONE, new BigDecimal("63500"), Side.BUY, "order-1");

        handler.onTradeExecuted(event);

        verify(positionService).applyTrade(event);
        verify(ledgerService).record(event);
    }
}
