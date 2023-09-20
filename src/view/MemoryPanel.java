package view;

import hardware.Memory;
import os.process_manager.Context;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends TextAreaPanel {

    // associations
    private Memory memory;

    public MemoryPanel() {
        this.textArea.setEditable(false);
        this.textArea.setDisabledTextColor(Color.WHITE);
        new MemoryPanelUpdater().start();
    }

    public void associate(Memory memory) {
        this.memory = memory;
    }

    private class MemoryPanelUpdater extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (memory != null) textArea.setText(memory.getStatus());
            }
        }
    }

}
