package visualizer.file_system_panel;

import os.file_system.FileSystem;
import visualizer.common.MiniVMColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FileSystemPanel extends JPanel {
    // associations
    private FileSystem fileSystem;
    // components
    private JTextArea diskImage;
    private JTextArea editor;

    public FileSystemPanel(FileSystem filesystem) {
        // set associations
        this.fileSystem = filesystem;
        // set attributes
        setBackground(MiniVMColor.BACKGROUND);
        setLayout(new BorderLayout());
        // create components
        JLabel title = new JLabel("File Manager");
        title.setFont(new Font("Arial", Font.PLAIN, 20));
        title.setForeground(Color.LIGHT_GRAY);
        title.setBorder(new EmptyBorder(20, 20, 10, 10));
        add(title, BorderLayout.NORTH);
        JPanel contents = new JPanel();
        contents.setLayout(new FlowLayout());
        contents.setBackground(MiniVMColor.BACKGROUND);
        diskImage = new JTextArea();
        diskImage.setEditable(false);
        diskImage.setEnabled(false);
        diskImage.setPreferredSize(new Dimension(500, 750));
        diskImage.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        diskImage.setDisabledTextColor(Color.LIGHT_GRAY);
        diskImage.setBackground(MiniVMColor.AREA);
        contents.add(diskImage);
        editor = new JTextArea();
        editor.setForeground(Color.LIGHT_GRAY);
        editor.setPreferredSize(new Dimension(1290, 750));
        editor.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        editor.setBackground(MiniVMColor.AREA);
        editor.setCaretColor(Color.LIGHT_GRAY);
        contents.add(editor);
        add(contents, BorderLayout.CENTER);
    }

}
