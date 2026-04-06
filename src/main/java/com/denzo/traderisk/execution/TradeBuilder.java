package com.denzo.traderisk.execution;

import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TradeBuilder {

    public List<Trade> buildTrades(Order order, List<OrderFill> fills) {
        List<Trade> trades = new ArrayList<>();
        for (OrderFill fill : fills) {
            Trade trade = new Trade(
                    order.getSymbol(),
                    fill.quantity(),
                    fill.price(),
                    order.getSide(),
                    fill.orderId()
            );
            trades.add(trade);
        }
        return trades;
    }
}
