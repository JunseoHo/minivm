package visualizer;

import com.formdev.flatlaf.FlatLightLaf;
import os.OperatingSystem;
import os.shell.Shell;
import visualizer.console.Console;
import visualizer.file_system_panel.FileSystemPanel;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {

    private FileSystemPanel fileSystemPanel;
    private Console console;

    public MiniVMVisualizer(OperatingSystem os, Shell shell) {
        // set attributes
        FlatLightLaf.setup();
        setTitle("MiniVM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(new BorderLayout());
        // create components
        add(fileSystemPanel = new FileSystemPanel(os.fileSystem), BorderLayout.CENTER);
        add(console = new Console(shell), BorderLayout.SOUTH);
    }


}
