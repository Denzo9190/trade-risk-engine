package com.denzo.traderisk.execution.simulation;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PartialFillModel {

    public List<OrderFill> generateFills(Order order, BigDecimal price) {
        List<OrderFill> fills = new ArrayList<>();

        BigDecimal total = order.getQuantity();
        BigDecimal first = total.multiply(BigDecimal.valueOf(0.4));
        BigDecimal second = total.multiply(BigDecimal.valueOf(0.3));
        BigDecimal third = total.subtract(first).subtract(second);

        fills.add(new OrderFill(order.getId(), price, first));
        fills.add(new OrderFill(order.getId(), price, second));
        fills.add(new OrderFill(order.getId(), price, third));

        return fills;
    }
}
