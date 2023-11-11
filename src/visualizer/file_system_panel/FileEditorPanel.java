package visualizer.file_system_panel;

import os.file_system.FileSystem;
import os.shell.Shell;
import visualizer.common.MiniVMColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FileEditorPanel extends JPanel {

    // associations
    private FileSystem fileSystem;
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
        buttonPanel.add(btnOpen);
        JButton btnSave = new JButton("Save");
        buttonPanel.add(btnSave);
        JButton btnClose = new JButton("Close");
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

}
