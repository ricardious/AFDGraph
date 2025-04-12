package com.afdgraph.lexer;

import com.afdgraph.models.LexicalException;
import com.afdgraph.models.Token;
import com.afdgraph.models.TokenType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void testValidInput() {
        String input = "AFD { name: \"MyAutomaton\", states: [S@0, S1] }";
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        List<Token> tokens = lexer.getTokens();
        List<LexicalException> errors = lexer.getErrors();

        tokens.forEach(t -> System.out.println("Token: " + t.getType() + " ('" + t.getLexeme() + "')"));
        errors.forEach(e -> System.out.println("Error: " + e.getErrorMessage() + " at line " + e.getLine() + ", column " + e.getColumn()));

        assertTrue(errors.isEmpty(), "There should be no lexical errors, but found: " + errors);
        assertEquals(14, tokens.size(), "Should recognize exactly 10 tokens");

        assertEquals(TokenType.AUTOMATON_KEYWORD.getDescription(), tokens.get(0).getType());
        assertEquals("AFD", tokens.get(0).getLexeme());
        assertEquals(TokenType.LEFT_BRACE.getDescription(), tokens.get(1).getType());
        assertEquals("{", tokens.get(1).getLexeme());
        assertEquals(TokenType.NAME_KEYWORD.getDescription(), tokens.get(2).getType());
        assertEquals("name", tokens.get(2).getLexeme());
        assertEquals(TokenType.COLON.getDescription(), tokens.get(3).getType());
        assertEquals(":", tokens.get(3).getLexeme());
        assertEquals(TokenType.STRING_LITERAL.getDescription(), tokens.get(4).getType());
        assertEquals("\"MyAutomaton\"", tokens.get(4).getLexeme());
    }

    @Test
    void testInvalidCharacter() {
        String input = "AFD { name: @Invalid }";
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        List<LexicalException> errors = lexer.getErrors();

        errors.forEach(e -> System.out.println("Detected error: " + e.getErrorMessage() +
                " at line " + e.getLine() + ", column " + e.getColumn()));

        assertFalse(errors.isEmpty(), "Lexer should detect invalid character");
        assertEquals(1, errors.size(), "Lexer should detect exactly one lexical error");
        assertTrue(errors.get(0).getErrorMessage().contains("@"), "Error should mention unexpected '@'");
    }

    @Test
    void testUnterminatedString() {
        String input = "name: \"Unterminated string";
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        List<LexicalException> errors = lexer.getErrors();

        errors.forEach(e -> System.out.println("Error: " + e.getErrorMessage() + " at line " + e.getLine() + ", column " + e.getColumn()));

        assertFalse(errors.isEmpty(), "Lexer should detect unterminated string literal");
        assertEquals(1, errors.size(), "Lexer should detect exactly one error");
        assertTrue(errors.get(0).getErrorMessage().contains("Unterminated string literal"), "Error should indicate unterminated string literal");
    }


    @Test
    void testCommentsAndWhitespace() {
        String input = "AFD { // this is a comment\n name: \"Automaton\" }";
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        List<Token> tokens = lexer.getTokens();
        List<LexicalException> errors = lexer.getErrors();

        tokens.forEach(t -> System.out.println("Token: " + t.getType() + " ('" + t.getLexeme() + "')"));
        errors.forEach(e -> System.out.println("Error: " + e.getErrorMessage() + " at line " + e.getLine() + ", column " + e.getColumn()));

        assertTrue(errors.isEmpty(), "Lexer should not produce errors for comments and whitespace");
        assertEquals(6, tokens.size(), "Lexer should correctly skip comments and whitespace");

        assertEquals(TokenType.AUTOMATON_KEYWORD.getDescription(), tokens.get(0).getType());
        assertEquals(TokenType.LEFT_BRACE.getDescription(), tokens.get(1).getType());
        assertEquals(TokenType.NAME_KEYWORD.getDescription(), tokens.get(2).getType());
        assertEquals(TokenType.COLON.getDescription(), tokens.get(3).getType());
        assertEquals(TokenType.STRING_LITERAL.getDescription(), tokens.get(4).getType());
    }

    @Test
    void testFileInput() throws IOException {
        String input = Files.readString(Paths.get("data/input/Entrada.lfp"));
        Lexer lexer = new Lexer(input);
        lexer.analyze();

        List<Token> tokens = lexer.getTokens();
        List<LexicalException> errors = lexer.getErrors();

        System.out.println("------ Recognized Tokens ------");
        tokens.forEach(t -> System.out.println("Token: " + t.getType() + " ('" + t.getLexeme() + "')"));

        if (!errors.isEmpty()) {
            System.out.println("------ Detected Lexical Errors ------");
            errors.forEach(e -> System.out.println("Error: " + e.getErrorMessage() +
                    " at line " + e.getLine() + ", column " + e.getColumn()));
        }

        assertFalse(tokens.isEmpty(), "Lexer should recognize tokens from the input file");
    }
}
