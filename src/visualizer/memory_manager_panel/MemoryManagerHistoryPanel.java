package visualizer.memory_manager_panel;

import os.memory_manager.MemoryManager;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

public class MemoryManagerHistoryPanel extends MiniVMPanel {

    private MemoryManager memoryManager;
    private JTextArea output;
    private Queue<String> histories = new LinkedList<>();

    public MemoryManagerHistoryPanel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        add(output = new MiniVMTextArea(1500, 250, false));
        updateStart();
    }

    @Override
    protected void updatePanel() {
        while (histories.size() > 12) histories.poll();
        String str =  memoryManager.getHistories().poll();
        if (str == null) return;
        histories.add(str);
        String text = "";
        for (String history : histories) text += history + "\n";
        output.setText(text);
    }
}
