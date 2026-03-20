package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionEventHandler {

    private final PositionService positionService;

    @EventListener
    public void handleTradeExecuted(TradeExecutedEvent event) {
        System.out.println(">>> PositionEventHandler: received event for trade " + event.tradeId());
        positionService.updatePosition(event.symbol(), event.quantity(), event.price());
    }
}
