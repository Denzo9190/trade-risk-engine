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
        positionService.updatePosition(
                event.symbol(),
                event.executedQuantity(),
                event.executedPrice()
        );
    }
}
