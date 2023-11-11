package visualizer.file_system_panel;

import common.MiniVMUtils;
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
    private JTextArea FAT;
    private JTextArea dataRegion;
    private FileEditorPanel editor;

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
        FAT = new JTextArea();
        FAT.setEditable(false);
        FAT.setEnabled(false);
        FAT.setPreferredSize(new Dimension(500, 750));
        FAT.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        FAT.setDisabledTextColor(Color.LIGHT_GRAY);
        FAT.setFont(new Font("Consolas", Font.PLAIN, 15));
        FAT.setBackground(MiniVMColor.AREA);
        contents.add(FAT);
        dataRegion = new JTextArea();
        dataRegion.setEditable(false);
        dataRegion.setEnabled(false);
        dataRegion.setPreferredSize(new Dimension(500, 750));
        dataRegion.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        dataRegion.setDisabledTextColor(Color.LIGHT_GRAY);
        dataRegion.setFont(new Font("Consolas", Font.PLAIN, 15));
        dataRegion.setBackground(MiniVMColor.AREA);
        contents.add(dataRegion);
        editor = new FileEditorPanel(filesystem);
        contents.add(editor);
        add(contents, BorderLayout.CENTER);
        new Thread(new Updater()).start();
    }

    private class Updater implements Runnable {
        @Override
        public void run() {
            while (true) {
                FAT.setText(fileSystem.getRecentChangedFAT());
                dataRegion.setText(fileSystem.getRecentChangedDiskImage());
                MiniVMUtils.sleep(200);
            }
        }
    }

}
