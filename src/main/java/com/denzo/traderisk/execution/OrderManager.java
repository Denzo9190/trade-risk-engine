package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderType;
import com.denzo.traderisk.strategy.SignalType;
import com.denzo.traderisk.strategy.TradingSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderManager {

    private final ExecutionAdapter executionAdapter;

    public Order createOrder(TradingSignal signal) {
        Side side = signal.type() == SignalType.BUY ? Side.BUY : Side.SELL;
        // Пока всегда MARKET, позже можно расширить
        return new Order(signal.symbol(), side, signal.quantity(), OrderType.MARKET);
    }

    public List<OrderFill> submitOrder(Order order) {
        log.debug("Submitting order: {}", order.getId());
        return executionAdapter.submitOrder(order);
    }
}
