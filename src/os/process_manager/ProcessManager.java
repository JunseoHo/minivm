package os.process_manager;

import hardware.cpu.CPU;
import hardware.cpu.Context;
import os.memory_manager.MemoryManager;
import os.memory_manager.Page;

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
    }

    public void load(List<Long> program) {
//        Page codeSegment = memoryManager.getPage();
//        Page dataSegment = memoryManager.getPage();
//        memoryManager.loadProgram(codeSegment, program);
////        Context context = new Context();
////        context.CS = codeSegment.base;
////        context.DS = dataSegment.base;
//        scheduler.admit(new Process(new Context(), codeSegment, dataSegment));
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
}
