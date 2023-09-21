package view;

import hardware.CPU;
import hardware.Memory;
import os.OperatingSystem;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // components
    private CPUPanel cpuPanel;
    private MemoryPanel memoryPanel;
    private Terminal terminal;

    public MainFrame() {
        this.setTitle("Mini-OS");
        this.setSize(1280, 960);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(cpuPanel = new CPUPanel(), 30, 30, 800, 400);
        this.add(memoryPanel = new MemoryPanel(), 860, 30, 380, 860);
        this.add(terminal = new Terminal(), 30, 460, 800, 430);
    }

    public void associate(OperatingSystem operatingSystem, CPU cpu, Memory memory) {
        this.terminal.associate(operatingSystem);
        this.cpuPanel.associate(cpu);
        this.memoryPanel.associate(memory);
    }

    private void add(Component c, int x, int y, int width, int height) {
        c.setBounds(x, y, width, height);
        add(c);
    }

}
