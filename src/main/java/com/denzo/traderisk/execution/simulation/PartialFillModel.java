package com.denzo.traderisk.execution.simulation;

import com.denzo.traderisk.execution.order.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PartialFillModel {

    /**
     * Возвращает список долей (количеств) для частичных филлов.
     * Сумма долей равна общему количеству ордера.
     */
    public List<BigDecimal> generateFillQuantities(Order order) {
        List<BigDecimal> quantities = new ArrayList<>();
        BigDecimal total = order.getQuantity();
        BigDecimal first = total.multiply(BigDecimal.valueOf(0.4));
        BigDecimal second = total.multiply(BigDecimal.valueOf(0.3));
        BigDecimal third = total.subtract(first).subtract(second);
        quantities.add(first);
        quantities.add(second);
        quantities.add(third);
        return quantities;
    }
}
