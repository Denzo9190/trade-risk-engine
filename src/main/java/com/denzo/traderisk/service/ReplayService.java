package com.denzo.traderisk.service;

import com.denzo.traderisk.event.DomainEvent;
import com.denzo.traderisk.event.EventStore;
import com.denzo.traderisk.event.TradeExecutedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplayService {

    private final EventStore eventStore;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;
    private final LedgerService ledgerService;

    public void replayAll() {
        // Сброс состояния (для простоты – пересоздание сервисов, но мы будем пересчитывать)
        // В реальной системе нужно сначала очистить все агрегаты.
        for (DomainEvent event : eventStore.getAll()) {
            if (event instanceof TradeExecutedEvent trade) {
                // Воспроизводим логику обработки сделки
                positionService.updatePosition(trade.symbol(), trade.quantity(), trade.price());
                realisedPnlService.calculateRealisedPnl(trade.symbol());
                // ledger записывается через слушателя событий, поэтому его не трогаем
            }
        }
    }
}
