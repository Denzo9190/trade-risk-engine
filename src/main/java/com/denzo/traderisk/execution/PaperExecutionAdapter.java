package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.simulation.ExecutionSimulator;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Primary
@Profile("!backtest")
@RequiredArgsConstructor
public class PaperExecutionAdapter implements ExecutionAdapter {

    private final ExecutionSimulator simulator;
    private final MarketDataEngine marketDataEngine;

    @Override
    public List<OrderFill> submitOrder(Order order) {
        log.info("Paper execution: submitting order {}", order.getId());
        order.markSubmitted();

        // Получаем реальную рыночную цену для символа
        BigDecimal referencePrice = marketDataEngine.getPrice(order.getSymbol());

        List<OrderFill> fills = simulator.simulate(order, referencePrice);
        fills.forEach(fill -> {
            log.info("Fill: {} {} @ {}", fill.quantity(), order.getSymbol(), fill.price());
            order.applyFill(fill.quantity());
        });

        log.info("Order {} final status: {}", order.getId(), order.getStatus());
        return fills;
    }
}
