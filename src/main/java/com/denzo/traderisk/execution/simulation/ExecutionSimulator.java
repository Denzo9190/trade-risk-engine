package com.denzo.traderisk.execution.simulation;

import com.denzo.traderisk.execution.order.Order;
import com.denzo.traderisk.execution.order.OrderFill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExecutionSimulator {

    private final SlippageModel slippageModel;
    private final LatencyModel latencyModel;
    private final PartialFillModel partialFillModel;

    public List<OrderFill> simulate(Order order, BigDecimal referencePrice) {
        latencyModel.simulateLatency();
        BigDecimal executionPrice = slippageModel.apply(referencePrice, order.getSide());
        return partialFillModel.generateFills(order, executionPrice);
    }
}
