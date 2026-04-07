package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderStatus;
import com.denzo.traderisk.execution.order.OrderType;
import com.denzo.traderisk.execution.simulation.ExecutionSimulator;
import com.denzo.traderisk.marketdata.MarketDataEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaperExecutionAdapterTest {

    @Mock
    private ExecutionSimulator simulator;

    @Mock
    private MarketDataEngine marketDataEngine;

    @InjectMocks
    private PaperExecutionAdapter adapter;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
    }

    @Test
    void shouldSubmitOrderAndReturnFills() {
        BigDecimal referencePrice = new BigDecimal("63500");
        when(marketDataEngine.getPrice(order.getSymbol())).thenReturn(referencePrice);

        List<OrderFill> simulatedFills = List.of(
                new OrderFill(order.getId(), new BigDecimal("63500"), BigDecimal.valueOf(0.4)),
                new OrderFill(order.getId(), new BigDecimal("63500"), BigDecimal.valueOf(0.3)),
                new OrderFill(order.getId(), new BigDecimal("63500"), BigDecimal.valueOf(0.3))
        );
        when(simulator.simulate(order, referencePrice)).thenReturn(simulatedFills);

        List<OrderFill> result = adapter.submitOrder(order);

        assertThat(result).hasSize(3);
        assertThat(order.getFilledQuantity()).isEqualByComparingTo("1");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
    }
}
