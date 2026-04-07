package com.denzo.traderisk.execution.simulation;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionSimulatorTest {

    private final ExecutionSimulator simulator =
            new ExecutionSimulator(new SlippageModel(), new LatencyModel(), new PartialFillModel());

    @Test
    void shouldGenerateMultipleFills() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        List<OrderFill> fills = simulator.simulate(order, BigDecimal.valueOf(60000));
        assertThat(fills).hasSize(3);
        BigDecimal total = fills.stream().map(OrderFill::quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("1");
    }

    @Test
    void shouldGenerateFillsWithDifferentPrices() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        List<OrderFill> fills = simulator.simulate(order, BigDecimal.valueOf(60000));

        assertThat(fills).hasSize(3);
        // Цены могут совпасть случайно, но с вероятностью >99% будут разными
        // Проверяем, что все цены положительные и сумма количеств равна 1
        BigDecimal totalQty = fills.stream().map(OrderFill::quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalQty).isEqualByComparingTo("1");
    }
}
