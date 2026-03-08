package com.denzo.traderisk.math;

import com.denzo.traderisk.config.FinancialConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Централизованный слой финансовой арифметики.
 * Все операции с BigDecimal, используемые в финансовых расчётах,
 * проходят через этот класс. Округление применяется только на границе вычислений
 * через метод {@link #money(BigDecimal)}.
 */
public final class FinancialMath {

    private FinancialMath() {}

    /**
     * Приводит значение к стандартному финансовому масштабу (PNL_SCALE) с округлением HALF_UP.
     * Используется для финального округления результатов вычислений.
     */
    public static BigDecimal money(BigDecimal value) {
        return value.setScale(FinancialConstants.PNL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Умножение без округления (округлять нужно через {@link #money}).
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * Сложение без округления.
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    /**
     * Вычитание без округления.
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }
}
