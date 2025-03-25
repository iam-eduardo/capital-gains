package org.finance;

import org.finance.application.services.TaxCalculationService;
import org.finance.domain.usecases.CalculateCapitalGainsTax;
import org.finance.infrastructure.io.CommandLineProcessor;
import org.finance.infrastructure.json.JsonParser;

public class Main {
    public static void main(String[] args) {
        // Create domain use case
        CalculateCapitalGainsTax calculateCapitalGainsTax = new CalculateCapitalGainsTax();

        // Create application service
        TaxCalculationService taxCalculationService = new TaxCalculationService(calculateCapitalGainsTax);

        // Create infrastructure components
        JsonParser jsonParser = new JsonParser();
        CommandLineProcessor commandLineProcessor = new CommandLineProcessor(jsonParser, taxCalculationService);

        // Start processing
        commandLineProcessor.process();
    }
}