package com.denzo.traderisk.controller;

import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ResponseEntity<Trade> create(@Valid @RequestBody CreateTradeRequest request,
                                        @RequestParam BigDecimal currentPrice) {
        Trade trade = tradeService.createTrade(request, currentPrice);
        return ResponseEntity.ok(trade);
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getAll() {
        return ResponseEntity.ok(tradeService.getAll());
    }

    @GetMapping("/unrealised-pnl")
    public ResponseEntity<List<PnLResponse>> getUnrealisedPnl(
            @RequestParam String symbol,  // ← теперь обязательный
            @RequestParam BigDecimal currentPrice) {
        List<PnLResponse> result = tradeService.getUnrealisedPnlBySymbol(symbol, currentPrice);
        return ResponseEntity.ok(result);
    }
}
