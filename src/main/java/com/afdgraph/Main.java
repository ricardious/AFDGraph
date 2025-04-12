package com.afdgraph;

import com.afdgraph.ui.MainWindow;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("FlatLaf Mac Dark not supported");
        }

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
