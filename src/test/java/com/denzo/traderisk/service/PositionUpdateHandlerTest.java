package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.PositionUpdatedEvent;
import com.denzo.traderisk.event.TradeExecutedEvent;
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
class PositionUpdateHandlerTest {

    @Mock
    private PositionService positionService;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private PositionUpdateHandler handler;

    @Test
    void shouldUpdatePositionAndLedgerOnTrade() {
        TradeExecutedEvent event = new TradeExecutedEvent(
                "BTCUSDT",
                BigDecimal.ONE,
                new BigDecimal("63500"),
                Side.BUY,
                "order-1"
        );

        PositionResponse mockPosition = new PositionResponse(
                "BTCUSDT",
                BigDecimal.ONE,
                new BigDecimal("63500"),
                BigDecimal.ZERO
        );
        when(positionService.getPosition("BTCUSDT")).thenReturn(mockPosition);

        handler.onTradeExecuted(event);

        verify(positionService).applyTrade(event);
        verify(ledgerService).record(event);
        verify(domainEventPublisher).publish(any(PositionUpdatedEvent.class));
    }
}
