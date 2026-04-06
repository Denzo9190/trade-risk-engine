package com.denzo.traderisk.execution.order;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void shouldCreateOrderWithNewStatus() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldTransitionThroughStates() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        order.markSubmitted();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
        order.markFilled();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
    }
}
