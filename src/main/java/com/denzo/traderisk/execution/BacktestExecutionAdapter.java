package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.marketdata.adapter.MarketDataAdapter;
import com.denzo.traderisk.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BacktestExecutionAdapter implements ExecutionAdapter {

    private final TimeProvider timeProvider;
    private final MarketDataAdapter historicalMarketDataAdapter;

    @Override
    public List<OrderFill> submitOrder(Order order) {
        BigDecimal fillPrice = historicalMarketDataAdapter.getPrice(order.getSymbol());
        log.debug("Backtest execution: order {} using historical price {} at {}", order.getId(), fillPrice, timeProvider.now());

        OrderFill fill = new OrderFill(order.getId(), fillPrice, order.getQuantity(), timeProvider.now());
        order.markSubmitted();
        order.applyFill(order.getQuantity());
        log.debug("Backtest execution: order {} filled at {} qty {}", order.getId(), fillPrice, order.getQuantity());
        return List.of(fill);
    }
}
