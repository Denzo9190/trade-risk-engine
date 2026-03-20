package com.denzo.traderisk.service.execution;

import com.denzo.traderisk.domain.Trade;
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
        System.out.println(">>> SignalExecutionService: executing signal for " + signal.symbol() + " price=" + signal.price());
        // 1. Создаём запрос для риск-движка
        TradeRequest riskRequest = new TradeRequest(
                signal.symbol(),
                signal.quantity(),
                signal.price(),
                signal.side()
        );

        // 2. Проверяем риск
        RiskCheckResult riskCheck = riskService.checkTrade(riskRequest);
        System.out.println(">>> SignalExecutionService: riskCheck.allowed=" + riskCheck.allowed());
        if (!riskCheck.allowed()) {
            System.out.println(">>> SignalExecutionService: risk rejected: " + riskCheck.reason());
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
        Trade trade = tradeService.createTrade(tradeRequest);
        System.out.println(">>> SignalExecutionService: trade created, id=" + trade.getId());
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
