package org.finance.domain.usecases;

import org.finance.domain.entities.Operation;
import org.finance.domain.entities.OperationType;
import org.finance.domain.entities.TaxResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateCapitalGainsTaxTest {

    private CalculateCapitalGainsTax calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculateCapitalGainsTax();
    }

    @Test
    void case1_shouldApplyTaxExemptionForSalesBelowThreshold() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 100),
                new Operation(OperationType.SELL, new BigDecimal("15.00"), 50),
                new Operation(OperationType.SELL, new BigDecimal("15.00"), 50)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
    }

    @Test
    void case2_shouldApplyTaxWhenSaleExceedsExemptionLimit() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("5.00"), 5000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("10000.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
    }

    @Test
    void case3_shouldDeductLossBeforeTaxingProfit() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("5.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 3000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("1000.0"), taxes.get(2).getTax());
    }

    @Test
    void case4_shouldHandleWeightedAverageWithoutProfit() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.BUY, new BigDecimal("25.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("15.00"), 10000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
    }

    @Test
    void case5_shouldApplyTaxAfterResettingWeightedAverage() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.BUY, new BigDecimal("25.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("15.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("25.00"), 5000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
        assertEquals(new BigDecimal("10000.0"), taxes.get(3).getTax());
    }

    @Test
    void case6_shouldAccumulateLossAndDeductFromFinalProfit() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("2.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 2000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 2000),
                new Operation(OperationType.SELL, new BigDecimal("25.00"), 1000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(3).getTax());
        assertEquals(new BigDecimal("3000.0"), taxes.get(4).getTax());
    }

    @Test
    void case7_shouldResetAverageAfterZeroQuantity() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("2.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 2000),
                new Operation(OperationType.SELL, new BigDecimal("20.00"), 2000),
                new Operation(OperationType.SELL, new BigDecimal("25.00"), 1000),
                new Operation(OperationType.BUY, new BigDecimal("20.00"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("15.00"), 5000),
                new Operation(OperationType.SELL, new BigDecimal("30.00"), 4350),
                new Operation(OperationType.SELL, new BigDecimal("30.00"), 650)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(3).getTax());
        assertEquals(new BigDecimal("3000.0"), taxes.get(4).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(5).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(6).getTax());
        assertEquals(new BigDecimal("3700.0"), taxes.get(7).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(8).getTax());
    }

    @Test
    void case8_shouldCalculateMassiveGainsCorrectly() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("10.0"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("50.0"), 10000),
                new Operation(OperationType.BUY, new BigDecimal("20.0"), 10000),
                new Operation(OperationType.SELL, new BigDecimal("50.0"), 10000)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("80000.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
        assertEquals(new BigDecimal("60000.0"), taxes.get(3).getTax());
    }

    @Test
    void case9_shouldPreserveLossesIfOperationIsExempt() {
        List<Operation> ops = List.of(
                new Operation(OperationType.BUY, new BigDecimal("5000.0"), 10),
                new Operation(OperationType.SELL, new BigDecimal("4000.0"), 5),
                new Operation(OperationType.BUY, new BigDecimal("15000.0"), 5),
                new Operation(OperationType.BUY, new BigDecimal("4000.0"), 2),
                new Operation(OperationType.BUY, new BigDecimal("23000.0"), 2),
                new Operation(OperationType.SELL, new BigDecimal("20000.0"), 1),
                new Operation(OperationType.SELL, new BigDecimal("12000.0"), 10),
                new Operation(OperationType.SELL, new BigDecimal("15000.0"), 3)
        );

        List<TaxResult> taxes = calculator.calculate(ops);

        assertEquals(new BigDecimal("0.0"), taxes.get(0).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(1).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(2).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(3).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(4).getTax());
        assertEquals(new BigDecimal("0.0"), taxes.get(5).getTax());
        assertEquals(new BigDecimal("1000.0"), taxes.get(6).getTax());
        assertEquals(new BigDecimal("2400.0"), taxes.get(7).getTax());
    }
}
