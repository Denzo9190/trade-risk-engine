package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.risk.engine.RiskDecision;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Component
public class PriceDeviationRule implements RiskRule {

    private final BigDecimal maxDeviation;

    public PriceDeviationRule(@Value("${risk.max-price-deviation:0.01}") BigDecimal maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    @Override
    public int priority() { return 20; }

    @Override
    public RiskDecision evaluate(RiskEvaluationContext ctx) {
        BigDecimal signalPrice = ctx.signal().price();
        BigDecimal marketPrice = ctx.currentPrice();
        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
            return RiskDecision.reject("Market price unavailable");
        }
        BigDecimal deviation = signalPrice.subtract(marketPrice).abs()
                .divide(marketPrice, 6, RoundingMode.HALF_UP);
        if (deviation.compareTo(maxDeviation) > 0) {
            String reason = String.format(Locale.US,
                    "Price deviation too high: signal=%.2f, market=%.2f, deviation=%.4f%%",
                    signalPrice, marketPrice, deviation.multiply(BigDecimal.valueOf(100)));
            return RiskDecision.reject(reason);
        }
        return RiskDecision.allow();
    }
}
