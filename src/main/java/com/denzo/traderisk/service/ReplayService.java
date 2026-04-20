package com.denzo.traderisk.service;

import com.denzo.traderisk.event.DomainEvent;
import com.denzo.traderisk.event.EventStore;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplayService {

    private final EventStore eventStore;
    private final PositionService positionService;
    private final PositionRepository positionRepository;
    private final RealisedPnlService realisedPnlService;
    private final LedgerService ledgerService;

    public void replayAll() {
        log.info("Starting replay of {} events", eventStore.getAll().size());

        // Очищаем состояние перед реплеем
        positionRepository.clear();

        for (DomainEvent event : eventStore.getAll()) {
            if (event instanceof TradeExecutedEvent trade) {
                positionService.applyTrade(trade);      // ✅ исправлено
                realisedPnlService.calculateRealisedPnl(trade.symbol());
            }
        }
        log.info("Replay completed");
    }
}
