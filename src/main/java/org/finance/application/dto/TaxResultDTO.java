package org.finance.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.finance.domain.entities.TaxResult;

import java.math.BigDecimal;

public class TaxResultDTO {
    @JsonProperty("tax")
    private BigDecimal tax;

    public TaxResultDTO(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public static TaxResultDTO fromDomain(TaxResult taxResult) {
        return new TaxResultDTO(taxResult.getTax());
    }
}
