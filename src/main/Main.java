package main;

import hardware.cpu.CPU;
import hardware.Memory;
import hardware.io_device.MiniOSFrame;
import os.OperatingSystem;
import os.SystemCall;
import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

import javax.swing.*;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        FileManager fileManager = new FileManager();
        List<Long> values = fileManager.getValues("SumOneToTen");
        CPU cpu = new CPU();
        new Thread(cpu).start();
        ProcessManager processManager = new ProcessManager(cpu);
        Memory memory = new Memory();
        cpu.associate(memory, processManager);
        MemoryManager memoryManager = new MemoryManager(memory);
        processManager.associate(memoryManager);
        SystemCall os = new OperatingSystem();
        MiniOSFrame miniOSFrame = new MiniOSFrame(cpu, memory, os);
        miniOSFrame.setVisible(true);
        processManager.load(values);
        processManager.load(values);
        processManager.load(values);
    }

}
