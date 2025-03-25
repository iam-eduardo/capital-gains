package org.finance.application.services;

import org.finance.application.dto.OperationDTO;
import org.finance.application.dto.TaxResultDTO;
import org.finance.domain.entities.Operation;
import org.finance.domain.entities.OperationType;
import org.finance.domain.entities.TaxResult;
import org.finance.domain.usecases.CalculateCapitalGainsTax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxCalculationServiceTest {

    @Mock
    private CalculateCapitalGainsTax calculateCapitalGainsTax;

    private TaxCalculationService taxCalculationService;

    @BeforeEach
    void setUp() {
        taxCalculationService = new TaxCalculationService(calculateCapitalGainsTax);
    }

    @Test
    void shouldCalculateTaxesForOperations() {
        // Given
        List<OperationDTO> operationDTOs = Arrays.asList(
                new OperationDTO("buy", new BigDecimal("10.0"), 100),
                new OperationDTO("sell", new BigDecimal("15.0"), 50)
        );

        List<TaxResult> domainResults = Arrays.asList(
                new TaxResult(new BigDecimal("0.0")),
                new TaxResult(new BigDecimal("0.0"))
        );

        when(calculateCapitalGainsTax.calculate(anyList())).thenReturn(domainResults);

        // When
        List<TaxResultDTO> results = taxCalculationService.calculateTaxes(operationDTOs);

        // Then
        assertEquals(2, results.size());
        assertEquals(0, results.get(0).getTax().compareTo(new BigDecimal("0.0")));
        assertEquals(0, results.get(1).getTax().compareTo(new BigDecimal("0.0")));

        // Verify interaction with domain use case
        verify(calculateCapitalGainsTax, times(1)).calculate(anyList());
    }

    @Test
    void shouldMapOperationDTOsToDomainEntities() {
        // Given
        List<OperationDTO> operationDTOs = Arrays.asList(
                new OperationDTO("buy", new BigDecimal("10.0"), 100),
                new OperationDTO("sell", new BigDecimal("15.0"), 50)
        );

        // Capture and verify the mapped domain entities
        when(calculateCapitalGainsTax.calculate(anyList())).thenAnswer(invocation -> {
            List<Operation> operations = invocation.getArgument(0);

            assertEquals(OperationType.BUY, operations.get(0).getType());
            assertEquals(0, operations.get(0).getUnitCost().compareTo(new BigDecimal("10.0")));
            assertEquals(100, operations.get(0).getQuantity());

            assertEquals(OperationType.SELL, operations.get(1).getType());
            assertEquals(0, operations.get(1).getUnitCost().compareTo(new BigDecimal("15.0")));
            assertEquals(50, operations.get(1).getQuantity());

            return Arrays.asList(
                    new TaxResult(new BigDecimal("0.0")),
                    new TaxResult(new BigDecimal("0.0"))
            );
        });

        // When
        taxCalculationService.calculateTaxes(operationDTOs);

        // Then
        verify(calculateCapitalGainsTax, times(1)).calculate(anyList());
    }
}
