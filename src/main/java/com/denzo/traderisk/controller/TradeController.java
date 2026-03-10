package com.denzo.traderisk.controller;

import com.denzo.traderisk.domain.Trade;
import com.denzo.traderisk.dto.CreateTradeRequest;
import com.denzo.traderisk.dto.PnLResponse;
import com.denzo.traderisk.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public ResponseEntity<Trade> create(@Valid @RequestBody CreateTradeRequest request) {
        Trade trade = tradeService.createTrade(request);
        return ResponseEntity.ok(trade);
    }

    @GetMapping
    public ResponseEntity<List<Trade>> getAll() {
        return ResponseEntity.ok(tradeService.getAll());
    }
}
