package visualizer.file_system_panel;

import os.file_system.FileSystem;

import javax.swing.*;
import java.awt.*;

public class FileSystemPanel extends JPanel {

    private FileSystem fileSystem;

    public FileSystemPanel(FileSystem filesystem) {
        this.fileSystem = filesystem;
        setBackground(new Color(51, 53, 55));
    }

}
