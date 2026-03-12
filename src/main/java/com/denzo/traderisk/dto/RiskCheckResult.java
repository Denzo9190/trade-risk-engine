package com.denzo.traderisk.dto;

public record RiskCheckResult(
        boolean allowed,
        String reason
) {
    public static RiskCheckResult ok() {
        return new RiskCheckResult(true, "OK");
    }

    public static RiskCheckResult rejected(String reason) {
        return new RiskCheckResult(false, reason);
    }
}
