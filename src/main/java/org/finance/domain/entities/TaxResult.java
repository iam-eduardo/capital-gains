package org.finance.domain.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxResult {
    private final BigDecimal tax;

    public TaxResult(BigDecimal tax) {
        // Guarantee 2 decimal places immediately
        this.tax = tax.setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal getTax() {
        return tax;
    }
}
