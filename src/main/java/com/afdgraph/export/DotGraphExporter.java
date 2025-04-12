package com.afdgraph.export;

import com.afdgraph.models.Automaton;
import com.afdgraph.models.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DotGraphExporter {

    public static void exportToDot(Automaton automaton, Path outputPath) throws IOException {
        String dotContent = generateDot(automaton);
        Files.writeString(outputPath, dotContent);
    }

    public static void exportToSvg(Automaton automaton, Path svgOutputPath) throws IOException {
        Path tempDot = Files.createTempFile("automaton_", ".dot");
        exportToDot(automaton, tempDot);

        ProcessBuilder pb = new ProcessBuilder(
                "dot", "-Tsvg", tempDot.toAbsolutePath().toString(), "-o", svgOutputPath.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(System.out::println);
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Graphviz 'dot' command failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Proceso de exportación SVG interrumpido", e);
        }

        Files.deleteIfExists(tempDot);
    }

    public static String generateDot(Automaton automaton) {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph ").append(escape(automaton.getName())).append(" {\n");
        sb.append("    rankdir=LR;\n");
        sb.append("    size=\"8,5\";\n");
        sb.append("    bgcolor=\"transparent\";\n");
        sb.append("    edge [color=\"white\", fontcolor=\"white\"];\n");
        sb.append("    node [fontname=\"Helvetica\", fontcolor=\"white\", color=\"white\", style=filled, gradientangle=90];\n");

        sb.append("    init [shape=point, color=white];\n");
        sb.append("    init -> ").append(escape(automaton.getInitialState().getName()))
                .append(" [color=\"white\"];\n");

        for (State state : automaton.getStates()) {
            String name = escape(state.getName());
            boolean isFinal = automaton.getFinalStates().contains(state);
            String shape = isFinal ? "doublecircle" : "circle";
            String fill = isFinal ? "red:yellow" : "blue:cyan";

            sb.append("    ")
                    .append(name)
                    .append(" [shape=").append(shape)
                    .append(", fillcolor=\"").append(fill).append("\"")
                    .append(", style=\"filled\"")
                    .append("];\n");
        }

        Map<State, Map<String, State>> transitions = automaton.getTransitions();
        for (Map.Entry<State, Map<String, State>> fromEntry : transitions.entrySet()) {
            State from = fromEntry.getKey();
            for (Map.Entry<String, State> trans : fromEntry.getValue().entrySet()) {
                String symbol = trans.getKey();
                State to = trans.getValue();

                sb.append("    ")
                        .append(escape(from.getName()))
                        .append(" -> ")
                        .append(escape(to.getName()))
                        .append(" [label=\"").append(symbol)
                        .append("\", color=\"white\"];\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}
