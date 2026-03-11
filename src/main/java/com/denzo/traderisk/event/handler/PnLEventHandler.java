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
        // При желании можно вызывать пересчёт realised PnL для символа.
        // Здесь для простоты просто считаем realised, но можно и не делать,
        // так как realised PnL можно получать по запросу.
        realisedPnlService.calculateRealisedPnl(event.symbol());
    }
}
