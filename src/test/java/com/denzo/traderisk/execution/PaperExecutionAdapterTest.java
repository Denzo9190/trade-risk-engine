package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderStatus;
import com.denzo.traderisk.execution.order.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaperExecutionAdapterTest {

    private final PaperExecutionAdapter adapter = new PaperExecutionAdapter();

    @Test
    void shouldSubmitAndReturnFullFill() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        List<OrderFill> fills = adapter.submitOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(fills).hasSize(1);
        OrderFill fill = fills.get(0);
        assertThat(fill.orderId()).isEqualTo(order.getId());
        assertThat(fill.quantity()).isEqualByComparingTo("1");
    }
}
