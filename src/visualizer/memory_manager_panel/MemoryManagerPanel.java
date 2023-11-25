package visualizer.memory_manager_panel;

import common.MiniVMUtils;
import os.memory_manager.MemoryManager;
import visualizer.common.MiniVMLabel;
import visualizer.common.MiniVMPanel;

import java.awt.*;

public class MemoryManagerPanel extends MiniVMPanel {

    private MemoryManager memoryManager;

    public MemoryManagerPanel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 40));
        add(new MiniVMLabel("Memory Manager"));
        add(new PageTablePanel(memoryManager));
        add(new RAMHistoryPanel(memoryManager));
        add(new MemoryManagerHistoryPanel(memoryManager));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        MiniVMUtils.sleep(200);
    }
}
