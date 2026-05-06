package com.denzo.traderisk.marketdata.registry;

import com.denzo.traderisk.marketdata.model.Symbol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SymbolRegistry {

    private final Set<String> supported;

    public SymbolRegistry(@Value("${market.symbols:BTCUSDT}") Set<String> symbols) {
        this.supported = symbols;
    }

    public boolean isSupported(String symbol) {
        return supported.contains(symbol);
    }

    public Set<String> getAll() {
        return supported;
    }

    // Новый метод, использующий тип Symbol
    public boolean isSupported(Symbol symbol) {
        return supported.contains(symbol.value());
    }

    public Set<Symbol> getAllSymbols() {
        return supported.stream().map(Symbol::new).collect(Collectors.toSet());
    }
}
