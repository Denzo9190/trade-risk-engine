package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.RiskCheckResult;
import com.denzo.traderisk.dto.TradeRequest;
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

        // Pre-trade risk check (теперь через TradeRequest)
        TradeRequest riskRequest = new TradeRequest(
                request.symbol(),
                request.quantity(),
                request.price(),
                request.side()
        );

        RiskCheckResult riskCheck = riskService.checkTrade(riskRequest);
        if (!riskCheck.allowed()) {
            throw new RiskViolationException(riskCheck.reason());
        }
        System.out.println(">>> TradeService: creating trade " + request.symbol() + " qty=" + request.quantity() + " price=" + request.price());
        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                request.side()
        );

        Trade saved = tradeRepository.save(trade);
        System.out.println(">>> TradeService: trade saved, id=" + saved.getId());
        TradeExecutedEvent event = new TradeExecutedEvent(
                saved.getId(),
                saved.getSymbol(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getSide()
        );
        System.out.println(">>> TradeService: publishing event for trade " + saved.getId());
        domainEventPublisher.publish(event);

        return saved;
    }

    /**
     * Возвращает все сделки.
     *
     * @return список всех сделок
     */
    public List<Trade> getAll() {
        List<Trade> trades = tradeRepository.findAll();
        System.out.println(">>> TradeService.getAll: " + trades.size() + " trades");
        return trades;
    }
}
