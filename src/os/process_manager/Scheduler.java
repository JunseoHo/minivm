package os.process_manager;

import hardware.cpu.CPU;
import hardware.cpu.Context;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable {
    // Computing resources
    private CPU cpu = null;
    // Attributes
    private Process runningProcess = null;
    private Queue<Process> readyQueue = null;
    private Queue<Process> blockQueue = null;

    public Scheduler(CPU cpu) {
        this.cpu = cpu;
        readyQueue = new LinkedList<>();
        blockQueue = new LinkedList<>();
    }

    public void admit(Process process) {
        readyQueue.add(process);
    }

    public void contextSwitch() {
        if (readyQueue.isEmpty()) return;
        runningProcess.setContext(cpu.getContext());
        readyQueue.add(runningProcess);
        runningProcess = readyQueue.poll();
        cpu.setContext(runningProcess.getContext());
    }

    public void release() {
//            memoryManager.addPage(runningProcess.getCodeSegment());
//            memoryManager.addPage(runningProcess.getDataSegment());
//            if (readyQueue.isEmpty()) {
//                runningProcess = null;
//                cpu.isRunning = false;
//            }else{
//                runningProcess = readyQueue.poll();
//                cpu.setContext(runningProcess.getContext());
//            }
    }

    @Override
    public void run() {

    }

    public void load(Process process) {
        if (runningProcess == null) {
            runningProcess = process;
            cpu.setContext(process.getContext());
        }
    }

    public void exit() {
        runningProcess = null;
        Context context = new Context(0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                false, false, false, false, false);
        cpu.setContext(context);
    }
}
