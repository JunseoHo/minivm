package os.process_manager;

import hardware.CPU;
import os.memory_manager.MemoryManager;
import os.memory_manager.Page;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessManager {

    // Associations
    private CPU cpu;
    private MemoryManager memoryManager;
    // Modules
    private Scheduler scheduler;

    public ProcessManager(CPU cpu) {
        this.cpu = cpu;
        scheduler = new Scheduler();
    }

    public void load(List<Long> program) {
        Page codeSegment = memoryManager.getPage();
        Page dataSegment = memoryManager.getPage();
        memoryManager.loadProgram(codeSegment, program);
        Context context = new Context();
        context.CS = codeSegment.base;
        context.DS = dataSegment.base;
        scheduler.admit(new Process(context, codeSegment, dataSegment));
    }

    public void associate(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public void contextSwitch() {
        scheduler.contextSwitch();
    }

    public void release() {
        scheduler.release();
    }

    private class Scheduler {

        private Process runningProcess = null;
        private Queue<Process> readyQueue = null;

        public Scheduler() {
            readyQueue = new LinkedList<>();
        }

        public void admit(Process process) {
            readyQueue.add(process);
            if (runningProcess == null) {
                runningProcess = readyQueue.poll();
                cpu.setContext(runningProcess.getContext());
            }
        }

        public void contextSwitch() {
            if (readyQueue.isEmpty()) return;
            runningProcess.setContext(cpu.getContext());
            readyQueue.add(runningProcess);
            runningProcess = readyQueue.poll();
            cpu.setContext(runningProcess.getContext());
        }

        public void release() {
            memoryManager.addPage(runningProcess.getCodeSegment());
            memoryManager.addPage(runningProcess.getDataSegment());
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.isRunning = false;
            }else{
                runningProcess = readyQueue.poll();
                cpu.setContext(runningProcess.getContext());
            }
        }
    }

    public void processInterrupt(int i) {
    }
}
