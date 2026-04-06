package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import java.util.List;

public interface ExecutionAdapter {
    List<OrderFill> submitOrder(Order order);
}
