package org.finance.application.services;

import org.finance.application.dto.OperationDTO;
import org.finance.application.dto.TaxResultDTO;
import org.finance.domain.entities.Operation;
import org.finance.domain.entities.TaxResult;
import org.finance.domain.usecases.CalculateCapitalGainsTax;

import java.util.List;
import java.util.stream.Collectors;

public class TaxCalculationService {
    private final CalculateCapitalGainsTax calculateCapitalGainsTax;

    public TaxCalculationService(CalculateCapitalGainsTax calculateCapitalGainsTax) {
        this.calculateCapitalGainsTax = calculateCapitalGainsTax;
    }

    public List<TaxResultDTO> calculateTaxes(List<OperationDTO> operationDTOs) {
        // Converter DTOs para entidades de domínio
        List<Operation> operations = operationDTOs.stream()
                .map(OperationDTO::toDomain)
                .collect(Collectors.toList());

        // Calcular impostos usando o caso de uso de domínio
        List<TaxResult> taxResults = calculateCapitalGainsTax.calculate(operations);

        // Converter resultados de domínio de volta para DTOs
        return taxResults.stream()
                .map(TaxResultDTO::fromDomain)
                .collect(Collectors.toList());
    }
}