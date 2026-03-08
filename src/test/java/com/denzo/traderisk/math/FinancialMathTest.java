package com.denzo.traderisk.math;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialMathTest {

    @Test
    void multiplyShouldApplyCorrectScale() {
        BigDecimal result = FinancialMath.multiply(new BigDecimal("2.123456789"), new BigDecimal("3.987654321"));
        assertEquals(8, result.scale());
    }
}
