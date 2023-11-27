package visualizer;

import com.formdev.flatlaf.FlatLightLaf;
import os.OperatingSystem;
import os.shell.Shell;
import visualizer.common.MiniVMPanel;
import visualizer.console.Console;
import visualizer.file_system_panel.FileSystemPanel;
import visualizer.memory_manager_panel.MemoryManagerPanel;
import visualizer.process_manager_panel.ProcessManagerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MiniVMVisualizer extends JFrame {

    private MiniVMPanel mainPanel;
    private ProcessManagerPanel processManagerPanel;
    private MemoryManagerPanel memoryManagerPanel;
    private FileSystemPanel fileSystemPanel;
    private Console console;

    public MiniVMVisualizer(OperatingSystem os, Shell shell) {
        // set attributes
        FlatLightLaf.setup();
        setTitle("MiniVM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLayout(new BorderLayout());
        addMouseListener(new MiniVMVisualizerKeyboardHandler());
        // create components
        mainPanel = new MiniVMPanel();
        mainPanel.setLayout(new CardLayout());
        mainPanel.add(new ProcessManagerPanel(os.processManager));
        mainPanel.add(new MemoryManagerPanel(os.memoryManager));
        mainPanel.add(new FileSystemPanel(os.fileSystem));
        add(mainPanel, BorderLayout.CENTER);
        add(console = new Console(shell), BorderLayout.SOUTH);
    }

    private class MiniVMVisualizerKeyboardHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            if (e.getButton() == MouseEvent.BUTTON1) cardLayout.previous(mainPanel);
            else if (e.getButton() == MouseEvent.BUTTON3) cardLayout.next(mainPanel);
        }
    }


}
