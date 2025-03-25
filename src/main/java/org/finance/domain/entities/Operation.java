package org.finance.domain.entities;

import java.math.BigDecimal;

public class Operation {
    private OperationType type;
    private BigDecimal unitCost;
    private long quantity;

    public Operation() {
    }

    public Operation(OperationType type, BigDecimal unitCost, long quantity) {
        this.type = type;
        this.unitCost = unitCost;
        this.quantity = quantity;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalCost() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
