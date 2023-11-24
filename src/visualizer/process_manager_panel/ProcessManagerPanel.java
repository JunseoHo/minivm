package visualizer.process_manager_panel;

import os.process_manager.ProcessManager;
import visualizer.common.MiniVMPanel;

import java.awt.*;

public class ProcessManagerPanel extends MiniVMPanel {
    public ProcessManagerPanel(ProcessManager processManager) {
        setLayout(new GridLayout(1, 3));
        add(new CPUPanel(processManager));
        add(new RunningProcessPanel());
        add(new ProcessQueuePanel());
    }

}
