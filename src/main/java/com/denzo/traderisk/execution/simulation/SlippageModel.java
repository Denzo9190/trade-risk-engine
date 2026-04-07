package com.denzo.traderisk.execution.simulation;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SlippageModel {

    private static final double MAX_SLIPPAGE = 0.0005; // 0.05%

    public BigDecimal apply(BigDecimal referencePrice) {
        double slip = ThreadLocalRandom.current().nextDouble(-MAX_SLIPPAGE, MAX_SLIPPAGE);
        BigDecimal factor = BigDecimal.valueOf(1 + slip);
        return referencePrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
