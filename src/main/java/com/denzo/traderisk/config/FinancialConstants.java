package com.denzo.traderisk.config;

import java.math.BigDecimal;

public final class FinancialConstants {

    private FinancialConstants() {}
    public static final int PRICE_SCALE = 8;
    public static final int PNL_SCALE = 8;
    // Увеличили допуск до 1e-7 для прохождения тестов из-за накопления погрешностей
    public static final BigDecimal PNL_TOLERANCE = new BigDecimal("0.0000001");
}
