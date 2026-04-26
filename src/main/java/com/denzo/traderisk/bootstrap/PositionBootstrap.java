package com.denzo.traderisk.bootstrap;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.service.PositionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PositionBootstrap {

    private final TradeRepository tradeRepository;
    private final PositionService positionService;

    @PostConstruct
    public void rebuildPositionsFromTrades() {
        log.info("Rebuilding positions from trade history...");
        List<Trade> allTrades = tradeRepository.findAll();
        allTrades.stream()
                .sorted(Comparator.comparing(Trade::getCreatedAt))
                .forEach(trade -> {
                    TradeExecutedEvent event = new TradeExecutedEvent(
                            trade.getSymbol(),
                            trade.getQuantity(),
                            trade.getPrice(),
                            trade.getSide(),
                            trade.getExchangeOrderId()
                    );
                    positionService.applyTrade(event);
                });
        log.info("Rebuilt {} positions from {} trades", positionService.getAllPositions().size(), allTrades.size());
    }
}
