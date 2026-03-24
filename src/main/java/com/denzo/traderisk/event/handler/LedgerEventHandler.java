package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.LedgerService;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class LedgerEventHandler {

    private final LedgerService ledgerService;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;

    @EventListener
    public void handleTradeExecuted(TradeExecutedEvent event) {
        PositionResponse position = positionService.getPosition(event.symbol());
        BigDecimal realisedPnl = realisedPnlService.calculateRealisedPnl(event.symbol()).realisedPnl();
        ledgerService.recordTrade(event, position, realisedPnl);
    }
}