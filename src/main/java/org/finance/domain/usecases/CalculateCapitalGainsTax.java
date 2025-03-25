package org.finance.domain.usecases;

import org.finance.domain.entities.Operation;
import org.finance.domain.entities.OperationType;
import org.finance.domain.entities.TaxResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the core business rules to calculate capital gains tax
 * based on a list of operations (buy/sell). Each operation results in a tax
 * output, following rules from the Nubank Capital Gains challenge.
 */
public class CalculateCapitalGainsTax {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    private static final BigDecimal TAX_EXEMPTION_THRESHOLD = new BigDecimal("20000.00");

    public List<TaxResult> calculate(List<Operation> operations) {
        List<TaxResult> taxes = new ArrayList<>();

        BigDecimal accumulatedLoss = BigDecimal.ZERO;
        BigDecimal weightedAveragePrice = BigDecimal.ZERO;
        long stockQuantity = 0;

        for (Operation op : operations) {
            BigDecimal tax = BigDecimal.ZERO;

            if (op.getType() == OperationType.BUY) {
                BigDecimal currentTotalCost = weightedAveragePrice.multiply(BigDecimal.valueOf(stockQuantity));
                BigDecimal newTotalCost = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));
                long newTotalQuantity = stockQuantity + op.getQuantity();

                if (newTotalQuantity > 0) {
                    weightedAveragePrice = currentTotalCost.add(newTotalCost)
                            .divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);
                } else {
                    weightedAveragePrice = BigDecimal.ZERO;
                }

                stockQuantity += op.getQuantity();

            } else if (op.getType() == OperationType.SELL) {
                BigDecimal totalSellValue = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));
                BigDecimal costBasis = weightedAveragePrice.multiply(BigDecimal.valueOf(op.getQuantity()));
                BigDecimal profit = totalSellValue.subtract(costBasis);

                // Always accumulate losses
                if (profit.compareTo(BigDecimal.ZERO) < 0) {
                    accumulatedLoss = accumulatedLoss.add(profit.abs());
                }

                // Only apply tax logic if sale is above exemption threshold
                if (totalSellValue.compareTo(TAX_EXEMPTION_THRESHOLD) > 0 && profit.compareTo(BigDecimal.ZERO) > 0) {
                    if (accumulatedLoss.compareTo(BigDecimal.ZERO) > 0) {
                        if (accumulatedLoss.compareTo(profit) >= 0) {
                            accumulatedLoss = accumulatedLoss.subtract(profit);
                            profit = BigDecimal.ZERO;
                        } else {
                            profit = profit.subtract(accumulatedLoss);
                            accumulatedLoss = BigDecimal.ZERO;
                        }
                    }

                    if (profit.compareTo(BigDecimal.ZERO) > 0) {
                        tax = profit.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
                    }
                }

                stockQuantity -= op.getQuantity();

                if (stockQuantity == 0) {
                    weightedAveragePrice = BigDecimal.ZERO;
                }
            }

            taxes.add(new TaxResult(tax));
        }

        return taxes;
    }
}