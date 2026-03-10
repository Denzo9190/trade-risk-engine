package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.dto.PositionResponse;
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
    private final PositionService positionService;
    private final RealisedPnlService realisedPnlService;
    private final LedgerService ledgerService;
    private final MarketPriceService marketPriceService; // добавлено


    /**
     * Создаёт новую сделку.
     *
     * @param request      данные сделки
     * @param currentPrice текущая рыночная цена (для расчёта unrealised PnL)
     * @return сохранённая сделка
     */
    @Transactional
    public Trade createTrade(CreateTradeRequest request) {
        // валидация
        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                Side.valueOf(request.side().toUpperCase())
        );
        Trade saved = tradeRepository.save(trade);

        // получаем текущую цену из сервиса
        BigDecimal currentPrice = marketPriceService.getCurrentPrice(saved.getSymbol());

        PositionResponse positionAfter = positionService.getPosition(saved.getSymbol());
        BigDecimal realisedPnlAfter = realisedPnlService.calculateRealisedPnl(saved.getSymbol()).realisedPnl();

        ledgerService.recordTrade(saved, positionAfter, realisedPnlAfter);

        return saved;
    }

    /**
     * Возвращает все сделки.
     */
    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }

    public List<PnLResponse> getUnrealisedPnlBySymbol(String symbol) {
        // Получаем позицию и конвертируем в PnLResponse
        PositionResponse pos = positionService.getPosition(symbol);
        return List.of(new PnLResponse(symbol, pos.unrealisedPnl(), pos.totalQuantity().intValue()));
    }
}
