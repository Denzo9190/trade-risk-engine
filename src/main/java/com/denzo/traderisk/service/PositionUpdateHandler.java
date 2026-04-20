package com.denzo.traderisk.service;

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

    @EventListener
    public void onTradeExecuted(TradeExecutedEvent event) {
        log.debug("PositionUpdateHandler received TradeExecutedEvent: {}", event);
        positionService.applyTrade(event);
        ledgerService.record(event);
    }
}
