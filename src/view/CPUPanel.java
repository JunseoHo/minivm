package view;

import hardware.CPU;
import os.process_manager.Context;

import javax.swing.*;
import java.awt.*;

public class CPUPanel extends TextAreaPanel {

    private CPU cpu;

    public CPUPanel() {
        this.textArea.setEditable(false);
        this.textArea.setDisabledTextColor(Color.WHITE);
        new CPUPanelUpdater().start();
    }

    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    private class CPUPanelUpdater extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (cpu == null) continue;
                Context context = cpu.getContext();
                String output = "[CPU Status]\n";
                output += "PC : " + context.PC;
                textArea.setText(output);
            }
        }
    }

}
