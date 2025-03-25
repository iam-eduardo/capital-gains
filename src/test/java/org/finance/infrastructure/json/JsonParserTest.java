package org.finance.infrastructure.json;

import static org.junit.jupiter.api.Assertions.*;

import org.finance.application.dto.OperationDTO;
import org.finance.application.dto.TaxResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    private JsonParser jsonParser;

    @BeforeEach
    void setUp() {
        jsonParser = new JsonParser();
    }

    @Test
    void shouldParseValidOperationsJson() {
        // Given
        String json = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":100}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":50}]";

        // When
        List<OperationDTO> operations = jsonParser.parseOperations(json);

        // Then
        assertEquals(2, operations.size());

        OperationDTO firstOp = operations.get(0);
        assertEquals("buy", firstOp.getOperation());
        assertEquals(new BigDecimal("10.00"), firstOp.getUnitCost());
        assertEquals(100, firstOp.getQuantity());

        OperationDTO secondOp = operations.get(1);
        assertEquals("sell", secondOp.getOperation());
        assertEquals(new BigDecimal("15.00"), secondOp.getUnitCost());
        assertEquals(50, secondOp.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenParsingInvalidJson() {
        // Given
        String invalidJson = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":100},";

        // When & Then
        assertThrows(RuntimeException.class, () -> jsonParser.parseOperations(invalidJson));
    }

    @Test
    void shouldSerializeTaxResultsToJson() {
        // Given
        List<TaxResultDTO> taxResults = Arrays.asList(
                new TaxResultDTO(BigDecimal.ZERO),
                new TaxResultDTO(new BigDecimal("10000.00"))
        );

        // When
        String json = jsonParser.serializeTaxResults(taxResults);

        // Then
        assertTrue(json.contains("\"tax\":0"));
        assertTrue(json.contains("\"tax\":10000.0"));
    }
}