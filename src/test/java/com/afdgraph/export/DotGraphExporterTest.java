package com.afdgraph.export;

import com.afdgraph.lexer.AutomatonParser;
import com.afdgraph.lexer.Lexer;
import com.afdgraph.models.Automaton;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DotGraphExporterTest {

    @Test
    void testExportDot() throws IOException {
        Path path = Paths.get("data/input/Entrada.lfp");
        String content = Files.readString(path);

        Lexer lexer = new Lexer(content);
        lexer.analyze();

        AutomatonParser parser = new AutomatonParser(lexer.getTokens());
        List<Automaton> automata = parser.parseAll();

        for (Automaton a : automata) {
            Path out = Path.of("test-output/" + a.getName() + ".dot");
            DotGraphExporter.exportToDot(a, out);
            System.out.println("Exported: " + out.toAbsolutePath());
        }
    }

}
