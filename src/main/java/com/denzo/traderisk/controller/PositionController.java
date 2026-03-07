package com.denzo.traderisk.controller;

import com.denzo.traderisk.dto.PnLReconciliationResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.dto.RealisedPnlResponse;
import com.denzo.traderisk.service.PnLReconciliationService;
import com.denzo.traderisk.service.PositionService;
import com.denzo.traderisk.service.RealisedPnlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    private final PnLReconciliationService pnlReconciliationService;

    @GetMapping("/{symbol}")
    public ResponseEntity<PositionResponse> getPosition(
            @PathVariable String symbol,
            @RequestParam BigDecimal currentPrice) {
        PositionResponse position = positionService.getPosition(symbol, currentPrice);
        return ResponseEntity.ok(position);
    }

    private final RealisedPnlService realisedPnlService; // Lombok подхватит через @RequiredArgsConstructor

    @GetMapping("/{symbol}/realised-pnl")
    public ResponseEntity<RealisedPnlResponse> getRealisedPnl(@PathVariable String symbol) {
        RealisedPnlResponse response = realisedPnlService.calculateRealisedPnl(symbol);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{symbol}/reconcile")
    public ResponseEntity<PnLReconciliationResponse> reconcile(
            @PathVariable String symbol,
            @RequestParam BigDecimal currentPrice) {
        PnLReconciliationResponse response = pnlReconciliationService.reconcile(symbol, currentPrice);
        return ResponseEntity.ok(response);
    }
}
