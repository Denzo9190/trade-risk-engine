package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.repository.TradeRepository;
import com.denzo.traderisk.strategy.Signal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    private final ExchangeAdapter exchangeAdapter;
    private final TradeRepository tradeRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void executeSignal(Signal signal) {
        // 1. Создаём запрос на биржу
        OrderRequest request = new OrderRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.side()
        );

        // 2. Отправляем на биржу (симулированную)
        OrderResult result = exchangeAdapter.placeOrder(request);

        // 3. Логируем исполнение
        log.info("Order executed: {} {} {} @ {} (orderId={})",
                signal.side(), signal.quantity(), signal.symbol(), result.executedPrice(), result.exchangeOrderId());

        // 4. Сохраняем сделку в БД (исторический факт) с exchangeOrderId
        Trade trade = new Trade(
                result.symbol(),
                result.executedQuantity(),
                result.executedPrice(),
                signal.side(),
                result.exchangeOrderId()
        );
        tradeRepository.save(trade);

        // 5. Публикуем событие для обновления позиций / PnL / Ledger
        TradeExecutedEvent event = new TradeExecutedEvent(
                result.symbol(),
                result.executedQuantity(),
                result.executedPrice(),
                signal.side(),
                result.exchangeOrderId()
        );
        domainEventPublisher.publish(event);
    }
}
