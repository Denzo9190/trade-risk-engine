package com.denzo.traderisk.controller;

import com.denzo.traderisk.service.InMemoryMarketPriceService;
import com.denzo.traderisk.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MarketPriceService marketPriceService;

    @PostMapping("/price/{symbol}")
    public void setPrice(@PathVariable String symbol, @RequestParam BigDecimal price) {
        if (marketPriceService instanceof InMemoryMarketPriceService) {
            ((InMemoryMarketPriceService) marketPriceService).updatePrice(symbol, price);
        }
    }
}
