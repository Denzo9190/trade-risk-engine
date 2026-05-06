package com.denzo.traderisk.risk.engine;

public record RiskDecision(boolean allowed, String reason) {
    public static RiskDecision allow() {
        return new RiskDecision(true, "OK");
    }
    public static RiskDecision reject(String reason) {
        return new RiskDecision(false, reason);
    }
}
