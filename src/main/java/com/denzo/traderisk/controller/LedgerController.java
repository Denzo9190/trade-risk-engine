package com.denzo.traderisk.controller;

import com.denzo.traderisk.domain.LedgerEntry;
import com.denzo.traderisk.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping
    public List<LedgerEntry> getAll() {
        return ledgerService.getAll();
    }

    @GetMapping("/{symbol}")
    public List<LedgerEntry> getBySymbol(@PathVariable String symbol,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        if (from != null && to != null) {
            return ledgerService.getHistory(symbol, from, to);
        }
        return ledgerService.getHistory(symbol);
    }
}
