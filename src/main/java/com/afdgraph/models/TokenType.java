package com.afdgraph.models;

public enum TokenType {
    // Automaton Definition Keywords
    AUTOMATON_KEYWORD("AUTOMATON"),
    NAME_KEYWORD("NAME"),
    DESCRIPTION_KEYWORD("DESCRIPTION"),
    STATES_KEYWORD("STATES"),
    ALPHABET_KEYWORD("ALPHABET"),
    INITIAL_STATE_KEYWORD("INITIAL"),
    FINAL_STATES_KEYWORD("FINAL"),
    TRANSITIONS_KEYWORD("TRANSITIONS"),

    // Structural Tokens
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    COMMA(","),
    COLON(":"),
    ARROW("->"),
    EQUALS("="),

    // Literal Types
    IDENTIFIER("IDENTIFIER"),
    STATE_IDENTIFIER("STATE_IDENTIFIER"),
    STRING_LITERAL("STRING"),
    NUMBER_LITERAL("NUMBER"),

    // Special Tokens
    WHITESPACE("WHITESPACE"),
    COMMENT("COMMENT"),

    // Error Token
    UNKNOWN("UNKNOWN"),
    ERROR("ERROR");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}