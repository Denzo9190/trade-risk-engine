package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
public class PaperExecutionAdapter implements ExecutionAdapter {

    @Override
    public Order submitOrder(Order order) {
        log.info("Paper execution: submitting order {} for {} {} {}",
                order.getId(), order.getSide(), order.getQuantity(), order.getSymbol());
        // Симуляция полного исполнения
        order.markSubmitted();
        order.markFilled();
        return order;
    }
}
