package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderType;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final ExecutionAdapter executionAdapter;
    private final TradeRepository tradeRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void execute(TradingSignal signal) {
        log.info("Executing signal: id={} {} {} {} @ {}",
                signal.id(), signal.type(), signal.quantity(), signal.symbol(), signal.price());

        // 1. Создаём ордер
        Side side = signal.type() == SignalType.BUY ? Side.BUY : Side.SELL;
        Order order = new Order(
                signal.symbol(),
                side,
                signal.quantity(),
                OrderType.MARKET   // пока market order
        );

        // 2. Отправляем на биржу через адаптер
        Order submittedOrder = executionAdapter.submitOrder(order);

        // 3. Создаём сделку (для упрощения: полное исполнение)
        Trade trade = new Trade(
                submittedOrder.getSymbol(),
                submittedOrder.getQuantity(),
                signal.price(),
                submittedOrder.getSide(),
                submittedOrder.getId()
        );
        tradeRepository.save(trade);

        // 4. Публикуем событие
        TradeExecutedEvent event = new TradeExecutedEvent(
                trade.getSymbol(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getSide(),
                trade.getExchangeOrderId()
        );
        domainEventPublisher.publish(event);
    }
}
