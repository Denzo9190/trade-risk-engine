package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import com.denzo.traderisk.execution.order.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Profile("!backtest")
public class PaperExecutionAdapter implements ExecutionAdapter {

    @Override
    public List<OrderFill> submitOrder(Order order) {
        log.info("Paper execution: submitting order {} for {} {} {} (type={})",
                order.getId(), order.getSide(), order.getQuantity(), order.getSymbol(), order.getType());

        order.markSubmitted();
        log.info("Paper execution: order {} SUBMITTED", order.getId());

        BigDecimal fillPrice = order.getType() == OrderType.LIMIT ? order.getPrice() : new BigDecimal("63500");
        OrderFill fill = new OrderFill(order.getId(), fillPrice, order.getQuantity());
        order.applyFill(order.getQuantity());
        log.info("Paper execution: order {} FILLED (filled={}/{})", order.getId(), order.getFilledQuantity(), order.getQuantity());
        return List.of(fill);
    }
}
