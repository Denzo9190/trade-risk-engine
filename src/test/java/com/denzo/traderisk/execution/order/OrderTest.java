package com.denzo.traderisk.execution.order;

import com.denzo.traderisk.domain.Side;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateMarketOrder() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.MARKET);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(order.getPrice()).isNull();
    }

    @Test
    void shouldCreateLimitOrderWithPrice() {
        Order order = new Order("BTCUSDT", Side.SELL, BigDecimal.valueOf(2), OrderType.LIMIT, new BigDecimal("63000"));
        assertThat(order.getPrice()).isEqualByComparingTo("63000");
    }

    @Test
    void shouldThrowWhenLimitOrderWithoutPrice() {
        assertThatThrownBy(() -> new Order("BTCUSDT", Side.BUY, BigDecimal.ONE, OrderType.LIMIT, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldApplyFullFill() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), OrderType.MARKET);
        order.applyFill(BigDecimal.valueOf(2));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(order.getFilledQuantity()).isEqualByComparingTo("2");
        assertThat(order.getRemainingQuantity()).isEqualByComparingTo("0");
    }

    @Test
    void shouldApplyPartialFill() {
        Order order = new Order("BTCUSDT", Side.BUY, BigDecimal.valueOf(2), OrderType.MARKET);
        order.applyFill(BigDecimal.ONE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PARTIALLY_FILLED);
        assertThat(order.getFilledQuantity()).isEqualByComparingTo("1");
        assertThat(order.getRemainingQuantity()).isEqualByComparingTo("1");
    }
}
