package os;

import hardware.cpu.CPU;
import hardware.memory.Memory;
import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

import java.util.Scanner;

import static java.lang.System.exit;

public class MiniOS extends SystemCall {
    // Associations
    private CPU cpu = null;
    private Memory memory = null;
    // Modules
    private ProcessManager processManager = null;
    private MemoryManager memoryManager = null;
    private FileManager fileManager = null;

    public MiniOS(String name) {
        super(name);
    }

    public void associate(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public void init() {
        processManager = new ProcessManager(cpu);
        memoryManager = new MemoryManager(memory);
        fileManager = new FileManager();
    }

    public void run() {
        new Thread(processManager).start();
        new Thread(memoryManager).start();
        System.out.println("Welcome to " + getName() + ".");
        while (true) {
            System.out.print("$> ");
            switch (new Scanner(System.in).nextLine()) {
                case "exit" -> {
                    System.out.println("Thank you for using " + getName() + ".");
                    return;
                }
                default -> System.out.println("Command not found.");
            }
        }
    }
}
