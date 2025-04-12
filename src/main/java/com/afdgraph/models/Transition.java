package com.afdgraph.models;

public class Transition {
    private State fromState;
    private String symbol;
    private State toState;

    public Transition(State fromState, String symbol, State toState) {
        this.fromState = fromState;
        this.symbol = symbol;
        this.toState = toState;
    }

    public State getFromState() {
        return fromState;
    }

    public void setFromState(State fromState) {
        this.fromState = fromState;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public State getToState() {
        return toState;
    }

    public void setToState(State toState) {
        this.toState = toState;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "fromState=" + fromState.getName() +
                ", symbol='" + symbol + '\'' +
                ", toState=" + toState.getName() +
                '}';
    }
}