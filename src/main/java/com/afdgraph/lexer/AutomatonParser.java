package com.afdgraph.lexer;

import com.afdgraph.models.Automaton;
import com.afdgraph.models.State;
import com.afdgraph.models.Token;
import com.afdgraph.models.TokenType;

import java.util.*;

public class AutomatonParser {
    private final List<Token> tokens;
    private int current;
    private final Map<String, State> statePool = new HashMap<>();
    private final Map<String, Automaton> automatonMap = new HashMap<>();

    public AutomatonParser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    public List<Automaton> parseAll() {
        automatonMap.clear();
        safeConsume(TokenType.LEFT_BRACE);
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            Automaton automaton = parseOneAutomaton();
            // Store only the latest instance of each automaton name
            automatonMap.put(automaton.getName(), automaton);
            if (!check(TokenType.RIGHT_BRACE)) safeConsume(TokenType.COMMA);
        }
        safeConsume(TokenType.RIGHT_BRACE);
        return new ArrayList<>(automatonMap.values());
    }

    private Automaton parseOneAutomaton() {
        Token nameToken = safeConsume(TokenType.IDENTIFIER);
        Automaton automaton = new Automaton(nameToken.getLexeme());
        safeConsume(TokenType.COLON);
        safeConsume(TokenType.LEFT_BRACE);

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.DESCRIPTION_KEYWORD)) {
                safeConsume(TokenType.COLON);
                Token value = safeConsume(TokenType.STRING_LITERAL);
                automaton.setDescription(stripQuotes(value.getLexeme()));
            } else if (match(TokenType.STATES_KEYWORD)) {
                safeConsume(TokenType.COLON);
                parseStates(automaton);
            } else if (match(TokenType.ALPHABET_KEYWORD)) {
                safeConsume(TokenType.COLON);
                parseAlphabet(automaton);
            } else if (match(TokenType.INITIAL_STATE_KEYWORD)) {
                safeConsume(TokenType.COLON);
                Token stateToken = consumeStateIdentifier();
                State state = getOrCreateState(stateToken.getLexeme());
                state.setInitial(true);
                automaton.setInitialState(state);
                automaton.addState(state);
            } else if (match(TokenType.FINAL_STATES_KEYWORD)) {
                safeConsume(TokenType.COLON);
                parseFinalStates(automaton);
            } else if (match(TokenType.TRANSITIONS_KEYWORD)) {
                safeConsume(TokenType.COLON);
                parseTransitions(automaton);
            } else {
                // Skip unknown tokens and try to recover
                advance();
            }

            if (!check(TokenType.RIGHT_BRACE)) safeConsume(TokenType.COMMA);
        }

        safeConsume(TokenType.RIGHT_BRACE);
        return automaton;
    }

    private void parseStates(Automaton automaton) {
        safeConsume(TokenType.LEFT_BRACKET);
        while (!check(TokenType.RIGHT_BRACKET) && !isAtEnd()) {
            Token stateToken = consumeStateIdentifier();
            State state = getOrCreateState(stateToken.getLexeme());
            automaton.addState(state);
            if (!check(TokenType.RIGHT_BRACKET)) safeConsume(TokenType.COMMA);
        }
        safeConsume(TokenType.RIGHT_BRACKET);
    }

    private void parseAlphabet(Automaton automaton) {
        safeConsume(TokenType.LEFT_BRACKET);
        while (!check(TokenType.RIGHT_BRACKET) && !isAtEnd()) {
            Token symbol = safeConsume(TokenType.STRING_LITERAL);
            automaton.addAlphabetSymbol(stripQuotes(symbol.getLexeme()));
            if (!check(TokenType.RIGHT_BRACKET)) safeConsume(TokenType.COMMA);
        }
        safeConsume(TokenType.RIGHT_BRACKET);
    }

    private void parseFinalStates(Automaton automaton) {
        safeConsume(TokenType.LEFT_BRACKET);
        while (!check(TokenType.RIGHT_BRACKET) && !isAtEnd()) {
            Token stateToken = consumeStateIdentifier();
            State state = getOrCreateState(stateToken.getLexeme());
            state.setFinal(true);
            automaton.addFinalState(state);
            automaton.addState(state);
            if (!check(TokenType.RIGHT_BRACKET)) safeConsume(TokenType.COMMA);
        }
        safeConsume(TokenType.RIGHT_BRACKET);
    }

    private void parseTransitions(Automaton automaton) {
        safeConsume(TokenType.LEFT_BRACE);
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            // Consume from state
            Token fromToken = consumeStateIdentifier();
            State fromState = getOrCreateState(fromToken.getLexeme());
            automaton.addState(fromState);

            // Safely look for equals sign
            safeConsume(TokenType.EQUALS);

            safeConsume(TokenType.LEFT_PARENTHESIS);
            while (!check(TokenType.RIGHT_PARENTHESIS) && !isAtEnd()) {
                try {
                    Token symbolToken = safeConsume(TokenType.STRING_LITERAL);
                    safeConsume(TokenType.ARROW);
                    Token toToken = consumeStateIdentifier();

                    State toState = getOrCreateState(toToken.getLexeme());
                    automaton.addState(toState);
                    automaton.addTransition(fromState, stripQuotes(symbolToken.getLexeme()), toState);
                } catch (Exception e) {
                    // Skip to next comma or closing parenthesis if there's an error in a transition
                    skipUntil(TokenType.COMMA, TokenType.RIGHT_PARENTHESIS);
                }

                if (!check(TokenType.RIGHT_PARENTHESIS)) safeConsume(TokenType.COMMA);
            }
            safeConsume(TokenType.RIGHT_PARENTHESIS);

            // Skip any extra characters after transition definitions
            skipExtraChars();

            if (!check(TokenType.RIGHT_BRACE)) safeConsume(TokenType.COMMA);
        }
        safeConsume(TokenType.RIGHT_BRACE);
    }

    private void skipExtraChars() {
        // Skip any characters that aren't structure-related until we hit a comma or closing brace
        while (!isAtEnd() && !check(TokenType.COMMA) && !check(TokenType.RIGHT_BRACE)) {
            advance();
        }
    }

    private void skipUntil(TokenType... types) {
        // Skip tokens until we find one of the specified types
        while (!isAtEnd()) {
            for (TokenType type : types) {
                if (check(type)) {
                    return;
                }
            }
            advance();
        }
    }

    private Token consumeStateIdentifier() {
        // Accept any identifier-like token as a state identifier
        if (isAtEnd()) {
            throw new RuntimeException("Unexpected end of input while parsing state identifier");
        }

        Token token = advance();
        // Clean up the lexeme to make it a valid state identifier
        String cleanLexeme = token.getLexeme().replaceAll("[^a-zA-Z0-9_]", "");
        if (cleanLexeme.isEmpty()) {
            cleanLexeme = "S_unknown";
        }
        return new Token(TokenType.STATE_IDENTIFIER, cleanLexeme, token.getLine(), token.getColumn());
    }

    private State getOrCreateState(String name) {
        return statePool.computeIfAbsent(name, State::new);
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType().equals(type);
    }

    private Token advance() {
        while (!isAtEnd()) {
            Token token = tokens.get(current++);
            if (token.getType() != TokenType.NUMBER_LITERAL) {
                return token;
            }
            System.out.println("Ignoring NUMBER_LITERAL: " + token.getLexeme());
        }
        return previous();
    }


    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token safeConsume(TokenType expectedType) {
        if (check(expectedType)) {
            return advance();
        }

        // Log the error but don't throw an exception
        System.err.println("Expected " + expectedType + " but got " +
                (isAtEnd() ? "END OF FILE" : peek().getType()) +
                " at position " + current);

        // Try to recover: find the next instance of the expected token type
        while (!isAtEnd() && !check(expectedType)) {
            advance();
        }

        if (isAtEnd()) {
            // Create a synthetic token if we couldn't find the expected type
            return new Token(expectedType, "", -1, -1);
        }
        return advance();
    }

    private String stripQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}