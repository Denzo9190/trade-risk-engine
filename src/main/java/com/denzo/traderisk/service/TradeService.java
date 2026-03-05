package com.denzo.traderisk.service;

import com.denzo.traderisk.domain.Side;
import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradeService {

    private final TradeRepository tradeRepository;

    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    public Trade createTrade(CreateTradeRequest request) {

        Trade trade = new Trade(
                request.symbol(),
                request.quantity(),
                request.price(),
                Side.valueOf(request.side().toUpperCase())
        );

        return tradeRepository.save(trade);
    }

    public List<Trade> getAll() {
        return tradeRepository.findAll();
    }

    // ---- расчёт unrealised PnL для одной сделки ----
    public BigDecimal calculateUnrealisedPnl(Trade trade, BigDecimal currentPrice) {
        BigDecimal priceDiff = trade.getSide() == Side.BUY
                ? currentPrice.subtract(trade.getPrice())
                : trade.getPrice().subtract(currentPrice);
        return trade.getQuantity().multiply(priceDiff);
    }

    // ---- расчёт суммарного unrealised PnL для списка сделок ----
    public BigDecimal calculateTotalUnrealisedPnl(List<Trade> trades, BigDecimal currentPrice) {
        return trades.stream()
                .map(t -> calculateUnrealisedPnl(t, currentPrice))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---- агрегация по символам ----
    public List<PnLResponse> getUnrealisedPnlBySymbol(String symbol, BigDecimal currentPrice) {
        List<Trade> trades = (symbol == null || symbol.isBlank())
                ? tradeRepository.findAll()
                : tradeRepository.findBySymbol(symbol);

        Map<String, List<Trade>> grouped = trades.stream()
                .collect(Collectors.groupingBy(Trade::getSymbol));

        return grouped.entrySet().stream()
                .map(entry -> new PnLResponse(
                        entry.getKey(),
                        calculateTotalUnrealisedPnl(entry.getValue(), currentPrice),
                        entry.getValue().size()))
                .toList();
    }
}
