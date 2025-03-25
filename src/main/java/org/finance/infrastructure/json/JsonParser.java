package org.finance.infrastructure.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finance.application.dto.OperationDTO;
import org.finance.application.dto.TaxResultDTO;

import java.util.List;

public class JsonParser {
    private final ObjectMapper objectMapper;

    public JsonParser() {
        this.objectMapper = new ObjectMapper();
    }

    public List<OperationDTO> parseOperations(String json) {
        try {
            // Clean up the input - remove whitespace between the JSON elements
            json = json.replaceAll("\\s+", " ").trim();
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, OperationDTO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing operations JSON: " + e.getMessage(), e);
        }
    }

    public String serializeTaxResults(List<TaxResultDTO> taxResults) {
        try {
            // Configurar o ObjectMapper para sempre mostrar zeros decimais
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

            return objectMapper.writeValueAsString(taxResults);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing tax results to JSON", e);
        }
    }
}