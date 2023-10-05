package hardware.io_device;

import hardware.cpu.CPU;
import hardware.memory.Memory;
import main.MiniOSUtil;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class MiniOSFrame extends JFrame {

    // Associations
    private CPU cpu = null;
    private Memory memory = null;
    // Components
    private MiniOSTextArea cpuStatus;
    private MiniOSTextArea memoryStatus;
    private MiniOSTerminal terminal;

    public MiniOSFrame(CPU cpu, Memory memory, SystemCall sysCall) {
        this.cpu = cpu;
        this.memory = memory;
        setTitle("MiniOS");
        setSize(1280, 960);
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        add(cpuStatus = new MiniOSTextArea(), 30, 30, 800, 300);
        add(memoryStatus = new MiniOSTextArea(), 860, 30, 380, 860);
        add(terminal = new MiniOSTerminal(sysCall), 30, 360, 800, 530);
        new Updater().start();
    }

    public void add(Component c, int x, int y, int width, int height) {
        c.setBounds(x, y, width, height);
        add(c);
    }

    private class Updater extends Thread {
        @Override
        public void run() {
            while (true) {
                cpuStatus.setText(cpu.status());
                memoryStatus.setText(memory.status());
                MiniOSUtil.sleep(100);
            }
        }
    }

}
