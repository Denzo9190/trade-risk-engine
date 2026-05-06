package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.risk.engine.RiskDecision;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class MaxExposureRule implements RiskRule {

    private final BigDecimal maxExposure;

    public MaxExposureRule(@Value("${risk.max-exposure:500000}") BigDecimal maxExposure) {
        this.maxExposure = maxExposure;
    }

    @Override
    public int priority() { return 40; }

    @Override
    public RiskDecision evaluate(RiskEvaluationContext ctx) {
        BigDecimal tradeExposure = ctx.signal().quantity().abs()
                .multiply(ctx.currentPrice())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal newTotal = ctx.portfolio().totalExposure().add(tradeExposure);
        if (newTotal.compareTo(maxExposure) > 0) {
            return RiskDecision.reject("Total exposure limit exceeded (max $" + maxExposure + ")");
        }
        return RiskDecision.allow();
    }
}
