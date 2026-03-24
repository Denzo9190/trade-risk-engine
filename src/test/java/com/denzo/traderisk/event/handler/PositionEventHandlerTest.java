package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.PositionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PositionEventHandlerTest {

    @Mock
    private PositionService positionService;

    @InjectMocks
    private PositionEventHandler handler;

    @Test
    void shouldUpdatePositionOnTradeEvent() {
        TradeExecutedEvent event = new TradeExecutedEvent("BTCUSDT", BigDecimal.valueOf(2), BigDecimal.valueOf(60000), Side.BUY, "1");
        handler.handleTradeExecuted(event);
        verify(positionService).updatePosition(event.symbol(), event.executedQuantity(), event.executedPrice());
    }
}
