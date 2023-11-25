package visualizer.memory_manager_panel;

import os.memory_manager.MemoryManager;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class RAMHistoryPanel extends MiniVMPanel {

    private MemoryManager memoryManager;
    private JTextArea output;
    private Queue<String> histories = new LinkedList<>();

    public RAMHistoryPanel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        add(output = new MiniVMTextArea(1500, 250, false));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        while (histories.size() > 12) histories.poll();
        histories.add(memoryManager.getRAMHistories().poll());
        String text = "";
        for (String history : histories) text += history + "\n";
        output.setText(text);
    }

}
