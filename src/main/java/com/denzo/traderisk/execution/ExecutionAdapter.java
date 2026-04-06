package com.denzo.traderisk.execution;

import com.denzo.traderisk.execution.order.Order;

public interface ExecutionAdapter {
    Order submitOrder(Order order);
}
