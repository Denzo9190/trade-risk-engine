package com.denzo.traderisk.risk;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.PortfolioResponse;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MaxPortfolioExposureRule implements RiskRule {

    private final RiskLimits limits;

    @Override
    public RiskCheckResult check(TradeRequest trade, PortfolioResponse portfolio) {
        BigDecimal currentExposure = portfolio.totalExposure();
        BigDecimal tradeExposure = trade.quantity().abs().multiply(trade.price());
        BigDecimal newExposure = currentExposure.add(tradeExposure);

        if (newExposure.compareTo(limits.getMaxPortfolioExposure()) > 0) {
            return RiskCheckResult.rejected(
                    "Portfolio exposure limit exceeded (max $" + limits.getMaxPortfolioExposure() + ")"
            );
        }
        return RiskCheckResult.ok();
    }
}
