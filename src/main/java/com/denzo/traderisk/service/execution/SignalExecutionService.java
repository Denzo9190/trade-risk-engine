package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.service.RiskService;
import com.denzo.traderisk.service.TradeService;
import com.denzo.traderisk.strategy.Signal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис исполнения сигналов, генерируемых стратегиями.
 *
 * <p>Преобразует {@link Signal} в запрос на сделку, проверяет риск через {@link RiskService}
 * и при успехе вызывает {@link TradeService}. Стратегии никогда не должны вызывать TradeService напрямую.</p>
 */
@Service
public class SignalExecutionService {

    private final RiskService riskService;
    private final TradeService tradeService;

    public SignalExecutionService(RiskService riskService, TradeService tradeService) {
        this.riskService = riskService;
        this.tradeService = tradeService;
    }

    /**
     * Исполняет один сигнал.
     *
     * @param signal сигнал, сгенерированный стратегией
     * @throws RiskViolationException если проверка риска не пройдена
     */
    public void executeSignal(Signal signal) {
        // 1. Создаём запрос для риск-движка
        TradeRequest riskRequest = new TradeRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.side()
        );

        // 2. Проверяем риск
        RiskCheckResult riskCheck = riskService.checkTrade(riskRequest);
        if (!riskCheck.allowed()) {
            throw new RiskViolationException(riskCheck.reason());
        }

        // 3. Создаём запрос на сделку
        CreateTradeRequest tradeRequest = new CreateTradeRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.side()
        );

        // 4. Исполняем сделку
        tradeService.createTrade(tradeRequest);
    }

    /**
     * Исполняет список сигналов.
     *
     * @param signals список сигналов
     */
    public void executeSignals(List<Signal> signals) {
        signals.forEach(this::executeSignal);
    }
}
