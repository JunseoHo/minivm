package visualizer.memory_manager_panel;

import common.SyncQueue;
import os.memory_manager.MemoryManager;
import os.memory_manager.PageHistory;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

public class MemoryManagerHistoryPanel extends MiniVMPanel {

    private MemoryManager memoryManager;
    private JTextArea output;
    private SyncQueue<PageHistory> histories = new SyncQueue<>();

    public MemoryManagerHistoryPanel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        add(output = new MiniVMTextArea(1500, 250, false));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        while (histories.size() > 12) histories.poll();
        PageHistory pageHistory = memoryManager.getPageHistories().poll();
        if (pageHistory == null) return;
        histories.add(pageHistory);
        String text = "";
        for (PageHistory history : histories.getQueue()) {
            text += String.format("%-12s%-20s%s", history.method, "Size : " + history.size, "Result : " + history.bytes) + "\n";
        }
        output.setText(text);
    }
}
