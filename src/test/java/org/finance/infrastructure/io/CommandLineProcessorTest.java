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

import static org.junit.jupiter.api.Assertions.*;

class CommandLineProcessorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final ByteArrayInputStream inContent = new ByteArrayInputStream("".getBytes());
    private final java.io.InputStream originalIn = System.in;

    private CommandLineProcessor commandLineProcessor;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));

        JsonParser jsonParser = new JsonParser();
        CalculateCapitalGainsTax calculateCapitalGainsTax = new CalculateCapitalGainsTax();
        TaxCalculationService taxCalculationService = new TaxCalculationService(calculateCapitalGainsTax);

        commandLineProcessor = new CommandLineProcessor(jsonParser, taxCalculationService);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void shouldProcessSimpleCase() {
        String input = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":100}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":50}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":50}]\n";

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        commandLineProcessor.process();

        String output = outContent.toString().trim();
        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]"));
    }

    @Test
    void shouldProcessProfitCase() {
        String input = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":10000}," +
                "{\"operation\":\"sell\",\"unit-cost\":20.00,\"quantity\":5000}," +
                "{\"operation\":\"sell\",\"unit-cost\":5.00,\"quantity\":5000}]\n";

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        commandLineProcessor.process();

        String output = outContent.toString().trim();
        assertTrue(output.contains("[{\"tax\":0.0},{\"tax\":10000.0},{\"tax\":0.0}]"));
    }

    @Test
    void shouldProcessMultipleLines() {
        String input = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":100}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":50}," +
                "{\"operation\":\"sell\",\"unit-cost\":15.00,\"quantity\":50}]\n" +
                "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":10000}," +
                "{\"operation\":\"sell\",\"unit-cost\":20.00,\"quantity\":5000}," +
                "{\"operation\":\"sell\",\"unit-cost\":5.00,\"quantity\":5000}]\n";

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        commandLineProcessor.process();

        String output = outContent.toString().trim();
        String[] lines = output.split(System.lineSeparator());

        assertEquals(2, lines.length);
        assertTrue(lines[0].contains("[{\"tax\":0.0},{\"tax\":0.0},{\"tax\":0.0}]"));
        assertTrue(lines[1].contains("[{\"tax\":0.0},{\"tax\":10000.0},{\"tax\":0.0}]"));
    }

    @Test
    void shouldHandleInvalidJsonGracefully() {
        String input = "[{\"operation\":\"buy\",\"unit-cost\":10.00,\"quantity\":100},\n";

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        assertDoesNotThrow(() -> commandLineProcessor.process());
    }
}