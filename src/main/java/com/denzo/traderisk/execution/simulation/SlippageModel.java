package com.denzo.traderisk.execution.simulation;

import com.denzo.traderisk.domain.Side;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SlippageModel {

    private static final double MAX_SLIPPAGE = 0.0005; // 0.05%

    public BigDecimal apply(BigDecimal referencePrice, Side side) {
        double slip = ThreadLocalRandom.current().nextDouble(0, MAX_SLIPPAGE);
        BigDecimal factor = side == Side.BUY
                ? BigDecimal.valueOf(1 + slip)
                : BigDecimal.valueOf(1 - slip);
        return referencePrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
