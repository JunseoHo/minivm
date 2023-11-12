package visualizer.file_system_panel;

import os.file_system.FileSystem;
import visualizer.common.MiniVMColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class FileEditorPanel extends JPanel {

    // associations
    private FileSystem fileSystem;
    // attributes
    private String openedFileName = null;
    // components
    private JTextArea editor;

    public FileEditorPanel(FileSystem fileSystem) {
        // set associations
        this.fileSystem = fileSystem;
        // set attributes
        setBackground(MiniVMColor.BACKGROUND);
        setLayout(new BorderLayout());
        // create components
        editor = new JTextArea();
        editor.setForeground(Color.LIGHT_GRAY);
        editor.setPreferredSize(new Dimension(790, 720));
        editor.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        editor.setBackground(MiniVMColor.AREA);
        editor.setCaretColor(Color.LIGHT_GRAY);
        editor.setFont(new Font("Arial", Font.PLAIN, 25));
        add(editor, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setBackground(MiniVMColor.BACKGROUND);
        JButton btnOpen = new JButton("Open");
        btnOpen.setBackground(MiniVMColor.BORDER);
        btnOpen.setForeground(Color.LIGHT_GRAY);
        btnOpen.setBorderPainted(false);
        btnOpen.addActionListener(e -> open());
        buttonPanel.add(btnOpen);
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> save());
        buttonPanel.add(btnSave);
        JButton btnClose = new JButton("Close");
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void open() {
        if (openedFileName != null) {
            JOptionPane.showMessageDialog(null, openedFileName + " is opened.", "Open failed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String fileName = JOptionPane.showInputDialog("Input file name");
        String message = fileSystem.open(fileName);
        if (message != null) {
            JOptionPane.showMessageDialog(null, message, "Open failed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openedFileName = fileName;
        List<Byte> contents = fileSystem.getContents(fileName);
        String str = "";
        for (Byte c : contents) str = str + c;
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

}
