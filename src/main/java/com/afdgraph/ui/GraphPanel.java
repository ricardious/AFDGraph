package com.afdgraph.ui;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;

public class GraphPanel extends JPanel {
    private final JSVGCanvas svgCanvas;
    private Point lastDragPoint;

    public GraphPanel() {
        setLayout(new BorderLayout());

        svgCanvas = new JSVGCanvas();
        svgCanvas.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
        svgCanvas.setEnableZoomInteractor(true);
        svgCanvas.setEnablePanInteractor(true);

        svgCanvas.setBackground(UIManager.getColor("Panel.background"));
        svgCanvas.setOpaque(true);

        add(svgCanvas, BorderLayout.CENTER);
    }

    public void loadSvg(File file) {
        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(this, "SVG file not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            svgCanvas.setURI(file.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(this, "Invalid SVG path: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
