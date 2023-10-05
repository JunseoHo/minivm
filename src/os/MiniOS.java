package os;

import hardware.cpu.CPU;
import hardware.memory.Memory;
import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

public class MiniOS extends SystemCall {
    // Associations
    private CPU cpu = null;
    private Memory memory = null;
    // Modules
    private ProcessManager processManager = null;
    private FileManager fileManager = null;
    private MemoryManager memoryManager = null;

    public MiniOS(String name) {
        super(name);
    }

    public void associate(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public void init() {
        processManager = new ProcessManager(cpu);
        fileManager = new FileManager();
        memoryManager = new MemoryManager(memory);
    }


    @Override
    public void run() {
        System.out.println("Welcome to MiniOS.");
    }
}
