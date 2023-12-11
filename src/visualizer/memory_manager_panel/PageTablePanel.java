package visualizer.memory_manager_panel;

import os.memory_manager.MemoryManager;
import os.memory_manager.Page;
import visualizer.common.MiniVMColor;
import visualizer.common.MiniVMPanel;

import java.awt.*;

public class PageTablePanel extends MiniVMPanel {

    private MemoryManager memoryManager;

    public PageTablePanel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        setPreferredSize(new Dimension(1700, 100));
        updateStart();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GRAY);
        g.fillRect(20, 0, getWidth() - 40, getHeight() / 6);
        int totalLength = getWidth() - 40;
        Page[] pageTable = memoryManager.getPageTable();
        int blockWidth = totalLength / pageTable.length;
        for (int i = 0; i < pageTable.length; i++) {
            if (pageTable[i].inUsed) {
                g.setColor(MiniVMColor.HIGHLIGHT);
                g.fillRect(i * blockWidth + 20, 0, blockWidth, getHeight() / 6);
            }
        }
    }

    @Override
    protected void updatePanel() {
        repaint();
    }

}
