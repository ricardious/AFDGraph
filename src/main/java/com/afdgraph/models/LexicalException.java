package com.afdgraph.models;

public class LexicalException {
    private final String errorMessage;
    private final int line;
    private final int column;

    public LexicalException(String errorMessage, int line, int column) {
        this.errorMessage = errorMessage;
        this.line = line;
        this.column = column;
    }

    // Getters
    public String getErrorMessage() {
        return errorMessage;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "LexicalError{" +
                "errorMessage='" + errorMessage + '\'' +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}
