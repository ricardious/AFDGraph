package com.afdgraph.models;

import java.util.*;

public class Automaton {
    private String name;
    private String description;
    private Set<State> states;
    private Set<String> alphabet;
    private State initialState;
    private Set<State> finalStates;
    private final Map<State, Map<String, State>> transitions;

    public Automaton() {
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashMap<>();
    }

    public Automaton(String name) {
        this.name = name;
        this.states = new LinkedHashSet<>();
        this.alphabet = new LinkedHashSet<>();
        this.finalStates = new LinkedHashSet<>();
        this.transitions = new HashMap<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public void addState(State state) {
        states.add(state);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<String> alphabet) {
        this.alphabet = alphabet;
    }

    public void addAlphabetSymbol(String symbol) {
        alphabet.add(symbol);
    }

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public void setFinals(Set<State> finalStates) {
        this.finalStates = finalStates;
    }

    public void addFinalState(State finalState) {
        finalStates.add(finalState);
    }

    public void addTransition(State fromState, String symbol, State toState) {
        transitions.computeIfAbsent(fromState, k -> new HashMap<>())
                .put(symbol, toState);
    }

    public void setTransitions(List<Transition> transitionsList) {
        for (Transition t : transitionsList) {
            addTransition(t.getFromState(), t.getSymbol(), t.getToState());
        }
    }

    public State getTransition(State fromState, String symbol) {
        return transitions.getOrDefault(fromState, new HashMap<>()).get(symbol);
    }

    public Map<State, Map<String, State>> getTransitions() {
        return transitions;
    }

    @Override
    public String toString() {
        return "Automaton{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", states=" + states +
                ", alphabet=" + alphabet +
                ", initialState=" + initialState +
                ", finalStates=" + finalStates +
                '}';
    }
}