package visualizer;

import common.Utils;
import hardware.Memory;
import hardware.cpu.CPU;
import os.SWName;
import os.SystemCall;

import javax.swing.*;
import java.awt.*;

public class MiniVMVisualizer extends JFrame {
    // model
    private CPU cpu;
    private Memory memory;
    private SystemCall systemCall;
    // components
    private GenerateInterruptPanel generateInterruptPanel;
    private VisualPanel processManagerVisualizer = new VisualPanel(() -> processManagerStatus());
    private VisualPanel memoryManagerVisualizer = new VisualPanel(() -> memoryManagerStatus());
    private VisualPanel fileManagerVisualizer = new VisualPanel(() -> fileManagerStatus());
    private VisualPanel ioManagerVisualizer = new VisualPanel(() -> ioManagerStatus());

    public MiniVMVisualizer(CPU cpu, Memory memory, SystemCall systemCall) {
        // set attributes
        this.cpu = cpu;
        this.memory = memory;
        this.systemCall = systemCall;
        generateInterruptPanel = new GenerateInterruptPanel(systemCall);
        setTitle("MiniVM");
        setSize(1680, 680);
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 3));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // add components
        add(generateInterruptPanel, BorderLayout.WEST);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(processManagerVisualizer);
        panel.add(memoryManagerVisualizer);
        panel.add(fileManagerVisualizer);
        panel.add(ioManagerVisualizer);
        add(panel, BorderLayout.CENTER);
        // start updater
        new Thread(new Updater()).start();
    }

    private void update() {
        processManagerVisualizer.update();
        memoryManagerVisualizer.update();
        fileManagerVisualizer.update();
        ioManagerVisualizer.update();
    }

    private String processManagerStatus() {
        return systemCall.status(SWName.PROCESS_MANAGER);
    }

    private String memoryManagerStatus() {
        return systemCall.status(SWName.MEMORY_MANAGER);
    }

    private String fileManagerStatus() {
        return systemCall.status(SWName.FILE_MANAGER);
    }

    private String ioManagerStatus() {
        return systemCall.status(SWName.IO_MANAGER);
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            while (true) {
                update();
                //Utils.sleep(10);
            }
        }
    }


}
