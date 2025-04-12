package com.afdgraph.lexer;

import com.afdgraph.models.LexicalException;
import com.afdgraph.models.Token;
import com.afdgraph.models.TokenType;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int currentPosition;
    private int line;
    private int column;
    private final List<Token> tokens;
    private final List<LexicalException> errors;

    public Lexer(String input) {
        this.input = input;
        this.currentPosition = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public void analyze() {
        while (currentPosition < input.length()) {
            // Skip whitespaces and comments
            if (skipWhitespaceAndComments()) continue;

            // Check if we've reached the end after skipping
            if (currentPosition >= input.length()) break;

            char currentChar = input.charAt(currentPosition);

            // Match specific token types
            if (matchStructuralTokens(currentChar)) continue;
            if (matchKeywords(currentChar)) continue;
            if (matchStringLiterals(currentChar)) continue;
            if (matchIdentifiers(currentChar)) continue;
            if (matchNumberLiterals(currentChar)) continue;

            // If no match, add as error
            addErrorToken(currentChar);
        }
    }

    private boolean skipWhitespaceAndComments() {
        // Skip whitespaces
        while (currentPosition < input.length() &&
                Character.isWhitespace(input.charAt(currentPosition))) {
            if (input.charAt(currentPosition) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            currentPosition++;
        }

        // Skip single-line comments
        if (currentPosition < input.length() - 1 &&
                input.charAt(currentPosition) == '/' &&
                input.charAt(currentPosition + 1) == '/') {
            while (currentPosition < input.length() &&
                    input.charAt(currentPosition) != '\n') {
                currentPosition++;
                column++;
            }
            return true;
        }

        // Skip multi-line comments
        if (currentPosition < input.length() - 1 &&
                input.charAt(currentPosition) == '/' &&
                input.charAt(currentPosition + 1) == '*') {
            currentPosition += 2;
            column += 2;
            while (currentPosition < input.length() - 1 &&
                    !(input.charAt(currentPosition) == '*' &&
                            input.charAt(currentPosition + 1) == '/')) {
                if (input.charAt(currentPosition) == '\n') {
                    line++;
                    column = 1;
                } else {
                    column++;
                }
                currentPosition++;
            }
            if (currentPosition < input.length() - 1) {
                currentPosition += 2;  // Skip closing */
                column += 2;
            }
            return true;
        }

        return false;
    }

    private boolean matchStructuralTokens(char currentChar) {
        // Check for single-character structural tokens
        TokenType structuralToken = switch (currentChar) {
            case '(' -> TokenType.LEFT_PARENTHESIS;
            case ')' -> TokenType.RIGHT_PARENTHESIS;
            case '{' -> TokenType.LEFT_BRACE;
            case '}' -> TokenType.RIGHT_BRACE;
            case '[' -> TokenType.LEFT_BRACKET;
            case ']' -> TokenType.RIGHT_BRACKET;
            case ',' -> TokenType.COMMA;
            case ':' -> TokenType.COLON;
            case '=' -> TokenType.EQUALS;
            default -> null;
        };

        if (structuralToken != null) {
            addToken(structuralToken, String.valueOf(currentChar));
            currentPosition++;
            column++;
            return true;
        }

        // Check for multi-character tokens like ->
        if (currentPosition < input.length() - 1) {
            String twoCharToken = input.substring(currentPosition, currentPosition + 2);
            if (twoCharToken.equals("->")) {
                addToken(TokenType.ARROW, twoCharToken);
                currentPosition += 2;
                column += 2;
                return true;
            }
        }

        return false;
    }

    private boolean matchKeywords(char currentChar) {
        // Predefined keywords with their exact token types
        String[][] keywordMappings = {
                {"descripcion", TokenType.DESCRIPTION_KEYWORD.name()},
                {"estados", TokenType.STATES_KEYWORD.name()},
                {"alfabeto", TokenType.ALPHABET_KEYWORD.name()},
                {"inicial", TokenType.INITIAL_STATE_KEYWORD.name()},
                {"finales", TokenType.FINAL_STATES_KEYWORD.name()},
                {"transiciones", TokenType.TRANSITIONS_KEYWORD.name()}
        };

        // Check for keyword match
        for (String[] keywordMapping : keywordMappings) {
            String keyword = keywordMapping[0];
            if (input.startsWith(keyword, currentPosition)) {
                // Ensure it's a complete word (not part of an identifier)
                int nextPos = currentPosition + keyword.length();
                if (nextPos >= input.length() ||
                        !Character.isLetterOrDigit(input.charAt(nextPos))) {
                    // Use valueOf to convert string to TokenType
                    TokenType keywordType = TokenType.valueOf(keywordMapping[1]);
                    addToken(keywordType, keyword);
                    currentPosition += keyword.length();
                    column += keyword.length();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchIdentifiers(char currentChar) {
        if (Character.isLetter(currentChar) || currentChar == '_') {
            int start = currentPosition;
            int length = 0;
            int startLine = line;
            int startColumn = column;

            while (currentPosition < input.length() &&
                    (Character.isLetterOrDigit(input.charAt(currentPosition)) ||
                            input.charAt(currentPosition) == '_' ||
                            !Character.isWhitespace(input.charAt(currentPosition)) &&
                                    !isStructuralSymbol(input.charAt(currentPosition)))) {
                currentPosition++;
                length++;
                column++;
            }

            String raw = input.substring(start, start + length);
            String cleaned = raw.replaceAll("[^a-zA-Z0-9_]", "");

            if (cleaned.matches("S\\d+")) {
                addToken(TokenType.STATE_IDENTIFIER, cleaned);
            } else {
                addToken(TokenType.IDENTIFIER, cleaned);
            }

            if (!raw.equals(cleaned)) {
                addErrorToken("Invalid characters removed from identifier: " + raw, startLine, startColumn);
            }

            return true;
        }
        return false;
    }

    private boolean isStructuralSymbol(char c) {
        return "(){}[],:=->".contains(String.valueOf(c));
    }

    private boolean matchStringLiterals(char currentChar) {
        if (currentChar == '"') {
            int startLine = line;
            int startColumn = column;
            int start = currentPosition;

            currentPosition++;
            column++;

            while (currentPosition < input.length() && input.charAt(currentPosition) != '"') {
                if (input.charAt(currentPosition) == '\n') {
                    addErrorToken("Unterminated string literal", startLine, startColumn);
                    return true;
                }
                currentPosition++;
                column++;
            }

            if (currentPosition >= input.length()) {
                addErrorToken("Unterminated string literal", startLine, startColumn);
                return true;
            }

            currentPosition++;
            column++;

            String stringLiteral = input.substring(start, currentPosition);
            addToken(TokenType.STRING_LITERAL, stringLiteral);
            return true;
        }
        return false;
    }

    private void addErrorToken(String errorMessage, int line, int column) {
        errors.add(new LexicalException(errorMessage, line, column));
    }

    private boolean matchNumberLiterals(char currentChar) {
        // Match numeric literals (integers and decimals)
        if (Character.isDigit(currentChar)) {
            int start = currentPosition;
            boolean hasDecimal = false;

            // Continue while digits or decimal point
            while (currentPosition < input.length() &&
                    (Character.isDigit(input.charAt(currentPosition)) ||
                            input.charAt(currentPosition) == '.')) {
                if (input.charAt(currentPosition) == '.') {
                    if (hasDecimal) {
                        // Multiple decimal points not allowed
                        break;
                    }
                    hasDecimal = true;
                }
                currentPosition++;
                column++;
            }

            String numberLiteral = input.substring(start, currentPosition);
            addToken(TokenType.NUMBER_LITERAL, numberLiteral);
            return true;
        }
        return false;
    }

    private void addToken(TokenType type, String lexeme) {
        tokens.add(new Token(type, lexeme, line, column - lexeme.length()));
    }

    private void addErrorToken(char invalidChar) {
        String errorMsg = "Unexpected character: " + invalidChar;
        errors.add(new LexicalException(errorMsg, line, column));
        currentPosition++;
        column++;
    }

    private void addErrorToken(String errorMessage) {
        errors.add(new LexicalException(errorMessage, line, column));
    }

    // Getters
    public List<Token> getTokens() {
        return tokens;
    }

    public List<LexicalException> getErrors() {
        return errors;
    }

    // Utility method to print tokens (for debugging)
    public void printTokens() {
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token);
        }

        if (!errors.isEmpty()) {
            System.out.println("\nErrors:");
            for (LexicalException error : errors) {
                System.out.println(error);
            }
        }
    }
}