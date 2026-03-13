package com.denzo.traderisk.risk;

import com.denzo.traderisk.config.RiskLimits;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.dto.PortfolioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MaxTradeSizeRule implements RiskRule {

    private final RiskLimits limits;

    @Override
    public RiskCheckResult check(TradeRequest trade, PortfolioResponse portfolio) {
        if (trade.quantity().abs().compareTo(limits.maxTradeSize()) > 0) {
            return RiskCheckResult.rejected(
                    "Trade size exceeds limit (max " + limits.maxTradeSize() + ")"
            );
        }
        return RiskCheckResult.ok();
    }
}
