package com.denzo.traderisk.risk;

import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.dto.PortfolioResponse;

/**
 * Интерфейс правила риск-движка.
 * Каждое правило реализует одну проверку.
 */
public interface RiskRule {

    /**
     * Выполняет проверку сделки.
     * @param trade данные сделки
     * @param portfolio текущее состояние портфеля (все позиции)
     * @return результат проверки (разрешено/запрещено + причина)
     */
    RiskCheckResult check(TradeRequest trade, PortfolioResponse portfolio);
}
