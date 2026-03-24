package com.denzo.traderisk.event.handler;

import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.service.RealisedPnlService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PnLEventHandler {

    private final RealisedPnlService realisedPnlService;

    @EventListener
    public void handleTradeExecuted(TradeExecutedEvent event) {
        // Пересчитываем realised PnL для символа (опционально, можно просто вызвать)
        realisedPnlService.calculateRealisedPnl(event.symbol());
    }
}
