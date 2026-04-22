package com.denzo.traderisk.controller;

import com.denzo.traderisk.portfolio.PortfolioService;
import com.denzo.traderisk.portfolio.PortfolioSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public PortfolioSnapshot getPortfolio() {
        return portfolioService.getPortfolio();
    }
}
