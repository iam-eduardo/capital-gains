package org.finance.infrastructure.io;

import org.finance.application.dto.OperationDTO;
import org.finance.application.dto.TaxResultDTO;
import org.finance.application.services.TaxCalculationService;
import org.finance.infrastructure.json.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommandLineProcessor {
    private final JsonParser jsonParser;
    private final TaxCalculationService taxCalculationService;

    public CommandLineProcessor(JsonParser jsonParser, TaxCalculationService taxCalculationService) {
        this.jsonParser = jsonParser;
        this.taxCalculationService = taxCalculationService;
    }

    public void process() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Se linha vazia, pare
                if (line.trim().isEmpty()) {
                    break;
                }

                // Adicione a linha ao construtor de JSON
                jsonBuilder.append(line);

                // Verifique se o JSON está completo
                if (isJsonComplete(jsonBuilder.toString())) {
                    try {

                        String jsonInput = jsonBuilder.toString();
                        List<OperationDTO> operations = jsonParser.parseOperations(jsonInput);
                        List<TaxResultDTO> taxResults = taxCalculationService.calculateTaxes(operations);

                        // Imprima os resultados
                        String resultJson = jsonParser.serializeTaxResults(taxResults);
                        // Adicionar uma nova linha para separar as saídas
                        System.out.println();
                        System.out.print(resultJson);

                        // Limpe o construtor para a próxima entrada
                        jsonBuilder.setLength(0);
                    } catch (Exception e) {
                        System.err.println("Erro ao processar entrada: " + e.getMessage());
                        jsonBuilder.setLength(0);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler entrada: " + e.getMessage());
        }
    }
    private boolean isJsonComplete(String json) {
        int openBrackets = 0;
        int openBraces = 0;

        for (char c : json.toCharArray()) {
            if (c == '[') openBrackets++;
            if (c == ']') openBrackets--;
            if (c == '{') openBraces++;
            if (c == '}') openBraces--;
        }

        return openBrackets == 0 && openBraces == 0 && json.contains("]");
    }
}