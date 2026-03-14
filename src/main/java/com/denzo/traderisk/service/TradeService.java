package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.event.DomainEventPublisher;
import com.denzo.traderisk.event.TradeExecutedEvent;
import com.denzo.traderisk.exception.RiskViolationException;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final RiskService riskService;

    /**
     * Создаёт новую сделку.
     *
     * @param request данные сделки
     * @return сохранённая сделка
     */
    @Transactional
    public Trade createTrade(CreateTradeRequest request) {
        // Валидация
        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Предторговая проверка рисков
        RiskCheckResult riskCheck = riskService.checkTrade(
                request.symbol(),
                request.quantity(),
                request.price()
        );
        if (!riskCheck.allowed()) {
            throw new RiskViolationException(riskCheck.reason());
        }

        // Создание сущности
        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                request.side()
        );

        Trade saved = tradeRepository.save(trade);

        // Публикация события
        TradeExecutedEvent event = new TradeExecutedEvent(
                saved.getId(),
                saved.getSymbol(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getSide()
        );
        domainEventPublisher.publish(event);

        return saved;
    }

    /**
     * Возвращает все сделки.
     *
     * @return список всех сделок
     */
    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }
}
