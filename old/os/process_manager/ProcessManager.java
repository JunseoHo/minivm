package operating_system.process_manager;

import hardware.cpu.CPU;
import hardware.cpu.Context;
import operating_system.memory_manager.MemoryManager;
import operating_system.memory_manager.Page;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessManager {
    // Associations
    private CPU cpu;
    // Modules
    private Scheduler scheduler;

    public ProcessManager(CPU cpu) {
        this.cpu = cpu;
        scheduler = new Scheduler(cpu);
        new Thread(scheduler).start();
    }

    public void load(Process process) {
        scheduler.load(process);
    }

    public void contextSwitch() {
        scheduler.contextSwitch();
    }

    public void release() {
        scheduler.release();
    }

    public void start() {
        new Thread(scheduler).start();
    }

    public void processInterrupt(int i) {
    }

    public void exit() {
        scheduler.exit();
    }
}
