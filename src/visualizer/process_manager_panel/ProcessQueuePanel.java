package visualizer.process_manager_panel;

import os.process_manager.ProcessManager;
import visualizer.common.MiniVMLabel;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTable;
import os.process_manager.Process;

import javax.swing.*;
import java.awt.*;

public class ProcessQueuePanel extends MiniVMPanel {

    private final ProcessManager processManager;
    private final MiniVMTable readyQueue;
    private final MiniVMTable blockQueue;

    public ProcessQueuePanel(ProcessManager processManager) {
        setLayout(new GridLayout(2, 1));
        this.processManager = processManager;
        add(readyQueue = new MiniVMTable(new String[]{"ID", "Name"}));
        add(blockQueue = new MiniVMTable(new String[]{"ID", "Name"}));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        readyQueue.clear();
        for (Process process : processManager.getReadyQueue().getQueue())
            readyQueue.add(new String[]{Integer.toString(process.id), process.name});
        blockQueue.clear();
        for (Process process : processManager.getBlockQueue().getQueue())
            blockQueue.add(new String[]{Integer.toString(process.id), process.name});
    }

}
