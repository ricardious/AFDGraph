package com.afdgraph.models;

import java.util.Objects;

public class State {
    private String name;
    private boolean isFinal;
    private boolean isInitial;

    public State(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("State name cannot be null or empty");
        }
        this.name = name;
        this.isFinal = false;
        this.isInitial = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean isInitial) {
        this.isInitial = isInitial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(name, state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "State{" +
                "name='" + name + '\'' +
                ", isFinal=" + isFinal +
                ", isInitial=" + isInitial +
                '}';
    }
}