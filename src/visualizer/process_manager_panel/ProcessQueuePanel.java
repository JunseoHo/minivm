package visualizer.process_manager_panel;

import os.process_manager.ProcessManager;
import visualizer.common.MiniVMLabel;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTable;

import javax.swing.*;
import java.awt.*;

public class ProcessQueuePanel extends MiniVMPanel {

    private final ProcessManager processManager;
    private final String[] columnNames = {"ID", "Name"};
    private MiniVMTable readyQueue;
    private MiniVMTable blockQueue;

    public ProcessQueuePanel(ProcessManager processManager) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        this.processManager = processManager;
        add(readyQueue = new MiniVMTable(columnNames));
        add(blockQueue = new MiniVMTable(columnNames));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        readyQueue.clear();
    }

}
