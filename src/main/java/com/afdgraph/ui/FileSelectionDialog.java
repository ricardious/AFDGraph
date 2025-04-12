package com.afdgraph.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class FileSelectionDialog extends JFileChooser {
    private static final Preferences prefs = Preferences.userNodeForPackage(FileSelectionDialog.class);

    public FileSelectionDialog() {
        super();
        setDialogTitle("Select AFD File");
        setFileSelectionMode(JFileChooser.FILES_ONLY);

        String lastPath = prefs.get("lastDirectory", null);
        if (lastPath != null) {
            File lastDir = new File(lastPath);
            if (lastDir.exists() && lastDir.isDirectory()) {
                setCurrentDirectory(lastDir);
            }
        }
    }

    public File chooseFile(Component parent) {
        int result = showOpenDialog(parent);
        if (result == APPROVE_OPTION) {
            File selectedFile = getSelectedFile();
            prefs.put("lastDirectory", selectedFile.getParent());
            return selectedFile;
        }
        return null;
    }
}
