package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.math.FinancialMath;
import com.denzo.traderisk.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;
    private final LedgerService ledgerService;

    /**
     * Создаёт новую сделку.
     *
     * @param request      данные сделки
     * @param currentPrice текущая рыночная цена (для расчёта unrealised PnL)
     * @return сохранённая сделка
     */
    @Transactional
    public Trade createTrade(CreateTradeRequest request, BigDecimal currentPrice) {
        // Валидация
        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Создание сущности
        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                Side.valueOf(request.side().toUpperCase())
        );

        Trade saved = tradeRepository.save(trade);

        // После сохранения получаем актуальное состояние позиции и realised PnL
        PositionResponse positionAfter = positionService.getPosition(saved.getSymbol(), currentPrice);
        BigDecimal realisedPnlAfter = realisedPnlService.calculateRealisedPnl(saved.getSymbol()).realisedPnl();

        // Запись в аудиторский журнал
        ledgerService.recordTrade(saved, positionAfter, realisedPnlAfter);

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

    /**
     * Возвращает нереализованный PnL по символу (или по всем символам, если symbol не указан).
     * Устаревший метод, рекомендуется использовать PositionService.
     *
     * @param symbol       символ (может быть null для всех)
     * @param currentPrice текущая рыночная цена
     * @return список PnLResponse
     */
    public List<PnLResponse> getUnrealisedPnlBySymbol(String symbol, BigDecimal currentPrice) {
        // Если символ не указан, берём все символы из сделок
        List<String> symbols;
        if (symbol == null || symbol.isBlank()) {
            symbols = tradeRepository.findAll().stream()
                    .map(Trade::getSymbol)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            symbols = List.of(symbol);
        }

        return symbols.stream()
                .map(s -> {
                    PositionResponse pos = positionService.getPosition(s, currentPrice);
                    return new PnLResponse(s, pos.unrealisedPnl(), pos.totalQuantity().intValue()); // tradeCount – не точный, но для совместимости
                })
                .collect(Collectors.toList());
    }
}