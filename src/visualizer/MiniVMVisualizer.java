package visualizer;

import com.formdev.flatlaf.FlatLightLaf;
import os.OperatingSystem;
import os.shell.Shell;
import visualizer.console.Console;
import visualizer.file_system_panel.FileSystemPanel;
import visualizer.process_manager_panel.ProcessManagerPanel;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {

    private ProcessManagerPanel processManagerPanel;
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
        add(processManagerPanel = new ProcessManagerPanel(os.processManager), BorderLayout.CENTER);
//        add(fileSystemPanel = new FileSystemPanel(os.fileSystem), BorderLayout.CENTER);
        add(console = new Console(shell), BorderLayout.SOUTH);
    }


}
