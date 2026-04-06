package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final OrderManager orderManager;
    private final TradeBuilder tradeBuilder;
    private final TradeRepository tradeRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void execute(TradingSignal signal) {
        log.info("Executing signal: id={} {} {} {} @ {}",
                signal.id(), signal.type(), signal.quantity(), signal.symbol(), signal.price());

        // 1. Создаём ордер
        Order order = orderManager.createOrder(signal);

        // 2. Отправляем на биржу, получаем филлы
        List<OrderFill> fills = orderManager.submitOrder(order);

        // 3. Создаём сделки из филлов
        List<Trade> trades = tradeBuilder.buildTrades(order, fills);
        trades.forEach(tradeRepository::save);

        // 4. Публикуем событие для каждого трейда (или одно агрегированное)
        for (Trade trade : trades) {
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
}
