package visualizer.file_system_panel;

import os.file_system.FileSystem;
import visualizer.common.MiniVMButton;
import visualizer.common.MiniVMColor;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FileEditorPanel extends MiniVMPanel {

    // associations
    private final FileSystem fileSystem;
    // attributes
    private String openedFileName = null;
    // components
    private final JTextArea editor;

    public FileEditorPanel(FileSystem fileSystem) {
        // set associations
        this.fileSystem = fileSystem;
        // create components
        add(editor = new MiniVMTextArea(800, 720, true), BorderLayout.CENTER);
        JPanel buttonPanel = new MiniVMPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.add(new MiniVMButton("Open", e -> open()));
        buttonPanel.add(new MiniVMButton("Save", e -> save()));
        buttonPanel.add(new MiniVMButton("Close", e -> close()));
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void open() {
        if (openedFileName != null) {
            JOptionPane.showMessageDialog(null, openedFileName + " is already opened.", "Open failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String fileName = JOptionPane.showInputDialog("Input file name");
        String message = fileSystem.open(fileName);
        if (message != null) {
            JOptionPane.showMessageDialog(null, message, "Open failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Byte> contents = fileSystem.getContents(openedFileName = fileName);
        String str = "";
        for (Byte c : contents) str = str + Character.toString(c);
        editor.setText(str);
    }

    private void save() {
        if (openedFileName == null) {
            JOptionPane.showMessageDialog(null, "File is not opened.", "Save failed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<Byte> contents = new LinkedList<>();
        for (char c : editor.getText().toCharArray()) contents.add((byte) c);
        fileSystem.setContents(openedFileName, contents);
    }

    private void close() {
        if (openedFileName == null) {
            JOptionPane.showMessageDialog(null, "File is not opened.", "Close failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String message = fileSystem.close(openedFileName);
        if (message != null)
            JOptionPane.showMessageDialog(null, message, "Close failed", JOptionPane.ERROR_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, "File " + openedFileName + " is closed.", "Close success", JOptionPane.PLAIN_MESSAGE);
        openedFileName = null;
    }

}
