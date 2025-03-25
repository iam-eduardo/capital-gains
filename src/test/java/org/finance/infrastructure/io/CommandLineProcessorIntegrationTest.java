package org.finance.infrastructure.io;

import org.finance.application.services.TaxCalculationService;
import org.finance.domain.usecases.CalculateCapitalGainsTax;
import org.finance.infrastructure.json.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLineProcessorIntegrationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent;

    private CommandLineProcessor commandLineProcessor;

    public CommandLineProcessorIntegrationTest() {
        // Prepare test input
        String input = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":10000}," +
                "{\"operation\":\"sell\",\"unit-cost\":20.00,\"quantity\":5000}]\n" +
                "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":10000}," +
                "{\"operation\":\"sell\",\"unit-cost\":5.00,\"quantity\":5000}]\n";

        inContent = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setUp() {
        // Redirect stdin and stdout
        System.setOut(new PrintStream(outContent));
        System.setIn(inContent);

        // Create dependencies
        CalculateCapitalGainsTax calculateCapitalGainsTax = new CalculateCapitalGainsTax();
        TaxCalculationService taxCalculationService = new TaxCalculationService(calculateCapitalGainsTax);
        JsonParser jsonParser = new JsonParser();

        // Create system under test
        commandLineProcessor = new CommandLineProcessor(jsonParser, taxCalculationService);
    }

    @AfterEach
    void tearDown() {
        // Restore stdin and stdout
        System.setOut(originalOut);
    }

    @Test
    void shouldProcessInputAndProduceExpectedOutput() {
        // When
        commandLineProcessor.process();

        // Then
        String output = outContent.toString();

        // Corrigir os valores esperados com duas casas decimais
        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":10000.0}]"),
                "Output should contain the first set of tax results");

        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":0.0}]"),
                "Output should contain the second set of tax results");
    }

    @Test
    void shouldHandleLossCarryOver() {
        String input = "[{\"operation\":\"buy\",\"unit-cost\":20.00,\"quantity\":1000}," +
                "{\"operation\":\"sell\",\"unit-cost\":10.00,\"quantity\":1000}]\n" + // loss: -10k
                "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":1000}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":1000}]\n"; // profit: 5k, but offset by previous loss

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        outContent.reset();

        commandLineProcessor.process();
        String output = outContent.toString();

        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":0.0}]"),
                "Should show no tax due to loss");
        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":0.0}]"),
                "Should carry loss forward and offset gain");
    }

}