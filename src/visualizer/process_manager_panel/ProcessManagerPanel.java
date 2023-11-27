package visualizer.process_manager_panel;

import os.process_manager.ProcessManager;
import visualizer.common.MiniVMLabel;
import visualizer.common.MiniVMPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ProcessManagerPanel extends MiniVMPanel {
    public ProcessManagerPanel(ProcessManager processManager) {
        setLayout(new BorderLayout());
        add(new MiniVMLabel("Process Manager"), BorderLayout.NORTH);
        JPanel contentPanel = new MiniVMPanel(new GridLayout(1, 3));
        contentPanel.add(new CPUPanel(processManager));
        contentPanel.add(new RunningProcessPanel());
        contentPanel.add(new ProcessQueuePanel(processManager));
        add(contentPanel, BorderLayout.CENTER);
    }

}
