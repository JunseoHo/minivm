package visualizer.file_system_panel;

import common.MiniVMUtils;
import os.file_system.FileSystem;
import visualizer.common.MiniVMColor;
import visualizer.common.MiniVMLabel;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FileSystemPanel extends MiniVMPanel {
    // associations
    private final FileSystem fileSystem;
    // components
    private final JTextArea FAT;
    private final JTextArea dataRegion;

    public FileSystemPanel(FileSystem filesystem) {
        // set associations
        this.fileSystem = filesystem;
        // create components
        add(new MiniVMLabel("File Manager"), BorderLayout.NORTH);
        MiniVMPanel contents = new MiniVMPanel(new FlowLayout());
        contents.add(FAT = new MiniVMTextArea(250, 740, false));
        contents.add(dataRegion = new MiniVMTextArea(250, 740, false));
        contents.add(new FileEditorPanel(filesystem));
        add(contents, BorderLayout.CENTER);
        updateStart();
    }

    @Override
    protected void updatePanel() {
        FAT.setText(fileSystem.getRecentChangedFAT());
        dataRegion.setText(fileSystem.getRecentChangedData());
        MiniVMUtils.sleep(200);
    }

}
