package com.denzo.traderisk.marketdata.model;

public record Symbol(String value) {
    public Symbol {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Symbol value cannot be empty");
        }
    }
}
