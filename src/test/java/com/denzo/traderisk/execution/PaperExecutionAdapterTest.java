package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderStatus;
import com.denzo.traderisk.execution.order.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaperExecutionAdapterTest {

    private final PaperExecutionAdapter adapter = new PaperExecutionAdapter();

    @Test
    void shouldSubmitAndFillOrder() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        Order result = adapter.submitOrder(order);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(result.getId()).isEqualTo(order.getId());
    }
}
