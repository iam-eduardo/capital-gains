package org.finance.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.finance.domain.entities.Operation;
import org.finance.domain.entities.OperationType;

import java.math.BigDecimal;

public class OperationDTO {
    @JsonProperty("operation")
    private String operation;

    @JsonProperty("unit-cost")
    private BigDecimal unitCost;

    @JsonProperty("quantity")
    private long quantity;

    public OperationDTO() {
    }

    public OperationDTO(String operation, BigDecimal unitCost, long quantity) {
        this.operation = operation;
        this.unitCost = unitCost;
        this.quantity = quantity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public Operation toDomain() {
        return new Operation(
                "buy".equalsIgnoreCase(operation) ? OperationType.BUY : OperationType.SELL,
                unitCost,
                quantity
        );
    }
}