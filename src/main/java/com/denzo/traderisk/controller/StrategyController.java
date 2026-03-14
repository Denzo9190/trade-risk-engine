package com.denzo.traderisk.controller;

import com.denzo.traderisk.strategy.Signal;
import com.denzo.traderisk.strategy.StrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategies")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    @GetMapping("/run")
    public List<Signal> runStrategies(@RequestParam String symbol) {
        return strategyService.evaluateStrategies(symbol);
    }
}
