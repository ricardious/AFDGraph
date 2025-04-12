package com.afdgraph.lexer;

import com.afdgraph.models.Automaton;
import com.afdgraph.models.Token;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutomatonParserTest {

    @Test
    void testParserWithSimpleAutomaton() {
        String input = """
        {
            AFD1: {
                descripcion: "Example AFD",
                estados: [S0, S1],
                alfabeto: ["a", "b"],
                inicial: S0,
                finales: [S1],
                transiciones: {
                    S0 = ("a" -> S1),
                    S1 = ("b" -> S0)
                }
            }
        }
        """;

        // Run lexer
        Lexer lexer = new Lexer(input);
        lexer.analyze();
        List<Token> tokens = lexer.getTokens();

        // Run parser
        AutomatonParser parser = new AutomatonParser(tokens);
        List<Automaton> automata = parser.parseAll();

        // Print result
        for (Automaton automaton : automata) {
            System.out.println("Automaton: " + automaton.getName());
            System.out.println("Description: " + automaton.getDescription());
            System.out.println("States: " + automaton.getStates());
            System.out.println("Alphabet: " + automaton.getAlphabet());
            System.out.println("Initial: " + automaton.getInitialState());
            System.out.println("Finals: " + automaton.getFinalStates());
            System.out.println("Transitions:");
            for (var from : automaton.getStates()) {
                for (String symbol : automaton.getAlphabet()) {
                    var to = automaton.getTransition(from, symbol);
                    if (to != null) {
                        System.out.println("  " + from + " --" + symbol + "--> " + to);
                    }
                }
            }
        }
    }

    @Test
    void testParserFromInputFile() throws IOException {
        Path path = Paths.get("data/input/Entrada.lfp");
        assertTrue(Files.exists(path), "Input file does not exist: " + path);

        String content = Files.readString(path);

        // Run the lexer
        Lexer lexer = new Lexer(content);
        lexer.analyze();

        List<Token> tokens = lexer.getTokens();
        List<com.afdgraph.models.LexicalException> errors = lexer.getErrors();

        // Log for debug
        System.out.println("------ Recognized Tokens ------");
        tokens.forEach(t -> System.out.printf("Token: %-20s ('%s')%n", t.getType(), t.getLexeme()));

        if (!errors.isEmpty()) {
            System.out.println("------ Detected Lexical Errors ------");
            errors.forEach(e -> System.out.printf("Error: %s at line %d, column %d%n",
                    e.getErrorMessage(), e.getLine(), e.getColumn()));
        }

        if (!errors.isEmpty()) {
            System.out.println("⚠️ Lexical errors were found:");
            errors.forEach(e -> System.out.printf("  %s at line %d, column %d%n",
                    e.getErrorMessage(), e.getLine(), e.getColumn()));
        }


        // Parse automata
        AutomatonParser parser = new AutomatonParser(tokens);
        List<Automaton> automata = parser.parseAll();

        assertFalse(automata.isEmpty(), "Parser should return at least one automaton");

        // Print parsed automata
        for (Automaton automaton : automata) {
            System.out.println("\nParsed Automaton:");
            System.out.println(automaton);
        }
    }
}
