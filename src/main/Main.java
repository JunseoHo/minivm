package main;

import hardware.CPU;
import hardware.Memory;
import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        FileManager fileManager = new FileManager();
        List<Long> values = fileManager.getValues("SumOneToTen");
        CPU cpu = new CPU();
        new Thread(cpu).start();
        ProcessManager processManager = new ProcessManager(cpu);
        Memory memory = new Memory();
        cpu.associate(memory, processManager);
        MemoryManager memoryManager = new MemoryManager(memory);
        processManager.associate(memoryManager);
        processManager.load(values);
        processManager.load(values);
    }

}
