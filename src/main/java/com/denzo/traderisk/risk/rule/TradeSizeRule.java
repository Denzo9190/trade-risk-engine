package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.risk.engine.RiskDecision;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TradeSizeRule implements RiskRule {

    private final BigDecimal maxTradeSize;

    public TradeSizeRule(@Value("${risk.max-trade-size:5}") BigDecimal maxTradeSize) {
        this.maxTradeSize = maxTradeSize;
    }

    @Override
    public int priority() { return 10; }

    @Override
    public RiskDecision evaluate(RiskEvaluationContext ctx) {
        if (ctx.signal().quantity().abs().compareTo(maxTradeSize) > 0) {
            return RiskDecision.reject("Trade size exceeds limit (max " + maxTradeSize + ")");
        }
        return RiskDecision.allow();
    }
}
