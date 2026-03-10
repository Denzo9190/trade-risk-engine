package com.denzo.traderisk.controller;

import com.denzo.traderisk.dto.PnLReconciliationResponse;
import com.denzo.traderisk.dto.PositionResponse;
import com.denzo.traderisk.service.PnLReconciliationService;
import com.denzo.traderisk.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final PnLReconciliationService pnlReconciliationService;

    @GetMapping("/{symbol}")
    public PositionResponse getPosition(@PathVariable String symbol) {
        return positionService.getPosition(symbol);
    }

    @GetMapping("/{symbol}/reconcile")
    public PnLReconciliationResponse reconcile(@PathVariable String symbol) {
        return pnlReconciliationService.reconcile(symbol);
    }
}