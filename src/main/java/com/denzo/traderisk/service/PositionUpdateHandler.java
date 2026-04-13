package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.PositionUpdatedEvent;
import com.denzo.traderisk.event.TradeExecutedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PositionUpdateHandler {

    private final PositionService positionService;
    private final LedgerService ledgerService;
    private final DomainEventPublisher domainEventPublisher;

    @EventListener
    public void onTradeExecuted(TradeExecutedEvent event) {
        log.debug("PositionUpdateHandler received TradeExecutedEvent: {}", event);
        positionService.applyTrade(event);
        ledgerService.record(event);
        // Публикуем событие обновления позиции
        PositionResponse newPosition = positionService.getPosition(event.symbol());
        domainEventPublisher.publish(new PositionUpdatedEvent(
                newPosition.symbol(),
                newPosition.totalQuantity(),
                newPosition.averagePrice(),
                newPosition.totalQuantity().signum() >= 0 ? Side.BUY : Side.SELL
        ));
    }
}
