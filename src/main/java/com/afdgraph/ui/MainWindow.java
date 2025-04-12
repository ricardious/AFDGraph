package com.afdgraph.ui;

import com.afdgraph.lexer.AutomatonParser;
import com.afdgraph.lexer.Lexer;
import com.afdgraph.models.Automaton;
import com.afdgraph.models.Token;
import com.afdgraph.models.LexicalException;
import com.afdgraph.report.ReportGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class MainWindow extends JFrame {
    private JTextArea inputTextArea;
    private GraphPanel graphPanel;
    private JLabel titleLabel;
    private File lastDirectory = null;
    private JComboBox<String> selectAFDCombo;
    private DefaultComboBoxModel<String> comboBoxModel;
    private final List<Automaton> loadedAutomata = new ArrayList<>();
    private List<Token> tokens = new ArrayList<>();
    private List<LexicalException> errors = new ArrayList<>();


    public MainWindow() {
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        setTitle("AFDGraph by Ricardious");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Title Bar
        titleLabel = new JLabel("AFDGraph by Ricardious", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setOpaque(true);

        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        inputTextArea.setLineWrap(true);
        inputTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        graphPanel = new GraphPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        add(titleLabel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(inputTextArea));
        splitPane.setRightComponent(graphPanel);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton analyzeButton = createButton("Analyze File");
        analyzeButton.addActionListener(e -> openFileAndLoadContent());

        JButton generateButton = createButton("Generate Report");
        generateButton.addActionListener(e -> generateHtmlReport());

        comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement("No file loaded");

        selectAFDCombo = new JComboBox<>(comboBoxModel);
        selectAFDCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        selectAFDCombo.setEnabled(true);

        JButton graphButton = createButton("Graph");
        graphButton.addActionListener(e -> generateGraph());

        panel.add(analyzeButton);
        panel.add(generateButton);
        panel.add(selectAFDCombo);
        panel.add(graphButton);

        return panel;
    }

    private void openFileAndLoadContent() {
        FileSelectionDialog fileDialog = new FileSelectionDialog();
        if (lastDirectory != null) {
            fileDialog.setCurrentDirectory(lastDirectory);
        }

        File selectedFile = fileDialog.chooseFile(this);
        if (selectedFile != null) {
            lastDirectory = selectedFile.getParentFile();
            try {
                String newContent = Files.readString(selectedFile.toPath()).trim();

                if (newContent.startsWith("{") && newContent.endsWith("}")) {
                    newContent = newContent.substring(1, newContent.length() - 1).trim();
                }

                String processedContent = getString(newContent);

                inputTextArea.setText(processedContent);

                Lexer lexer = new Lexer(processedContent);
                lexer.analyze();

                tokens = lexer.getTokens();
                errors = lexer.getErrors();

                System.out.println("------ Recognized Tokens ------");
                tokens.forEach(token -> System.out.println("Token: " + token.getType() + " ('" + token.getLexeme() + "')"));

                if (!errors.isEmpty()) {
                    System.out.println("------ Lexical Errors Detected ------");
                    errors.forEach(err -> System.out.println("Error: " + err.getErrorMessage() +
                            " at line " + err.getLine() + ", column " + err.getColumn()));
                } else {
                    System.out.println("No lexical errors detected.");
                }

                AutomatonParser parser = new AutomatonParser(tokens);
                List<Automaton> parsedAutomata = parser.parseAll();

                loadedAutomata.clear();
                comboBoxModel.removeAllElements();

                for (Automaton automaton : parsedAutomata) {
                    loadedAutomata.add(automaton);
                    comboBoxModel.addElement(automaton.getName());
                }

                if (comboBoxModel.getSize() == 0) {
                    comboBoxModel.addElement("No file loaded");
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getString(String newContent) {
        String currentContent = inputTextArea.getText().trim();
        String processedContent;

        if (currentContent.isEmpty()) {
            processedContent = "{\n" + newContent + "\n}";
        }
        else {
            if (currentContent.startsWith("{") && currentContent.endsWith("}")) {
                currentContent = currentContent.substring(1, currentContent.length() - 1).trim();
            }

            Lexer newLexer = new Lexer(newContent);
            newLexer.analyze();
            AutomatonParser newParser = new AutomatonParser(newLexer.getTokens());
            List<Automaton> newAutomata = newParser.parseAll();

            List<String> newAutomataNames = new ArrayList<>();
            for (Automaton automaton : newAutomata) {
                newAutomataNames.add(automaton.getName());
            }

            String[] automataDefinitions = currentContent.split("(?<=}),");

            StringBuilder filteredContent = new StringBuilder();

            for (String definition : automataDefinitions) {
                boolean shouldKeep = true;

                for (String name : newAutomataNames) {
                    String pattern = name + ": {";
                    int nameIndex = definition.indexOf(pattern);

                    if (nameIndex >= 0 && nameIndex < 10) {
                        shouldKeep = false;
                        break;
                    }
                }

                if (shouldKeep) {
                    if (!filteredContent.isEmpty()) {
                        filteredContent.append(",\n");
                    }
                    filteredContent.append(definition);
                }
            }

            if (!filteredContent.isEmpty()) {
                processedContent = "{\n" + filteredContent.toString() + ",\n" + newContent + "\n}";
            } else {
                processedContent = "{\n" + newContent + "\n}";
            }
        }
        return processedContent;
    }

    private void generateGraph() {
        String selectedName = (String) selectAFDCombo.getSelectedItem();
        if (selectedName == null || selectedName.equals("No file loaded")) {
            JOptionPane.showMessageDialog(this, "No AFD selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Automaton selectedAutomaton = loadedAutomata.stream()
                .filter(a -> a.getName().equals(selectedName))
                .findFirst()
                .orElse(null);

        if (selectedAutomaton == null) {
            JOptionPane.showMessageDialog(this, "AFD not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File output = new File("data/output/" + selectedAutomaton.getName() + ".svg");
            if (!output.getParentFile().mkdirs() && !output.getParentFile().exists()) {
                JOptionPane.showMessageDialog(this, "Could not create the necessary directories",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            com.afdgraph.export.DotGraphExporter.exportToSvg(selectedAutomaton, output.toPath());

            graphPanel.loadSvg(output);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating SVG: " + ex.getMessage(), "SVG Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateHtmlReport() {
        if (tokens == null || tokens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tokens available. Please analyze a file first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String outputPath = "data/reports/lexical_report.html";
            File reportsDir = new File("data/reports");
            if (!reportsDir.mkdirs() && !reportsDir.exists()) {
                JOptionPane.showMessageDialog(this, "Could not create the necessary directories",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ReportGenerator.generateHtmlReport(tokens, errors, outputPath);

            Desktop.getDesktop().browse(new File(outputPath).toURI());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        return button;
    }
}
