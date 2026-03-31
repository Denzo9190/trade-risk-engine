package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final TradeRepository tradeRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void execute(TradingSignal signal) {
        log.info("Executing signal: id={} {} {} {} @ {}",
                signal.id(), signal.type(), signal.quantity(), signal.symbol(), signal.price());

        Side side = signal.type() == SignalType.BUY ? Side.BUY : Side.SELL;
        Trade trade = new Trade(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                side,
                UUID.randomUUID().toString()
        );

        Trade saved = tradeRepository.save(trade);

        TradeExecutedEvent event = new TradeExecutedEvent(
                saved.getSymbol(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getSide(),
                saved.getExchangeOrderId()
        );
        domainEventPublisher.publish(event);
    }
}
