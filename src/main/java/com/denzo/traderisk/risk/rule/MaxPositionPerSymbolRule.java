package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.risk.engine.RiskDecision;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MaxPositionPerSymbolRule implements RiskRule {

    private final BigDecimal maxPositionSize;

    public MaxPositionPerSymbolRule(@Value("${risk.max-position-size:10}") BigDecimal maxPositionSize) {
        this.maxPositionSize = maxPositionSize;
    }

    @Override
    public int priority() { return 30; }

    @Override
    public RiskDecision evaluate(RiskEvaluationContext ctx) {
        var position = ctx.portfolio().positions().get(ctx.signal().symbol());
        if (position == null) {
            return RiskDecision.allow();
        }
        BigDecimal newQuantity = position.quantity().add(ctx.signal().quantity());
        if (newQuantity.abs().compareTo(maxPositionSize) > 0) {
            return RiskDecision.reject("Position limit exceeded (max " + maxPositionSize + ")");
        }
        return RiskDecision.allow();
    }
}
