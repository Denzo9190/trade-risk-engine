package com.denzo.traderisk.risk.rule;

import com.denzo.traderisk.risk.engine.RiskDecision;
import com.denzo.traderisk.risk.engine.RiskEvaluationContext;

public interface RiskRule {
    int priority();
    RiskDecision evaluate(RiskEvaluationContext context);
}
