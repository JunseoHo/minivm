package os.process_manager;

import common.CircularQueue;
import exception.ProcessLoadException;
import hardware.HIQ;
import hardware.HWName;
import hardware.cpu.CPU;
import os.OSModule;
import os.SIQ;
import os.SWName;
import os.file_manager.File;
import os.file_manager.FileType;
import os.memory_manager.Page;

import java.util.List;

public class ProcessManager extends OSModule {
    // attributes
    private static final int MAX_CONCURRENT_PROCESS = 10;
    // hardware
    private CPU cpu;
    // components
    private Scheduler scheduler = new Scheduler();
    private Loader loader = new Loader();
    private CircularQueue<Integer> processIdQueue;

    public ProcessManager() {
        // create process id queue
        processIdQueue = new CircularQueue<>(MAX_CONCURRENT_PROCESS);
        for (int processId = 0; processId < processIdQueue.capacity(); processId++)
            processIdQueue.enqueue(processId);
        // register interrupt service routines
        registerInterruptServiceRoutine(SIQ.REQUEST_LOAD_PROCESS, (intr) -> loader.loadProcess(intr));
        registerInterruptServiceRoutine(SIQ.REQUEST_SWITCH_CONTEXT, (intr) -> scheduler.switchContext(intr));
        registerInterruptServiceRoutine(SIQ.REQUEST_TERMINATE_PROCESS, (intr) -> scheduler.terminate(intr));
    }

    @Override
    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    private class Scheduler {
        private Process runningProcess;
        private CircularQueue<Process> readyQueue = new CircularQueue<>();
        private CircularQueue<Process> blockQueue = new CircularQueue<>();

        public void admit(Process process) {
            if (runningProcess == null) {
                runningProcess = process;
                cpu.restore(process.save());
            } else readyQueue.enqueue(process);
        }

        public void terminate(SIQ intr) {
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.switchTasking();
            } else {
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            cpu.generateInterrupt(new HIQ(HWName.CPU, HIQ.RESPONSE_TERMINATE_PROCESS));
        }

        public void switchContext(SIQ intr) {
            if (runningProcess != null && !readyQueue.isEmpty()) {
                runningProcess.restore(cpu.save());
                readyQueue.enqueue(runningProcess);
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            cpu.generateInterrupt(new HIQ(HWName.CPU, HIQ.RESPONSE_SWITCH_CONTEXT));
        }

    }

    private class Loader {

        public void loadProcess(SIQ intr) {
            try {
                // resource allocation
                String fileName = (String) intr.values[0];
                send(new SIQ(SWName.FILE_MANAGER, SIQ.REQUEST_FILE, fileName));
                intr = receive(SIQ.RESPONSE_FILE, SIQ.FILE_NOT_FOUND);
                if (intr.id == SIQ.FILE_NOT_FOUND) throw new ProcessLoadException(intr.id);
                File file = (File) intr.values[0];
                if (file.type != FileType.EXECUTABLE) throw new ProcessLoadException(intr.id);
                send(new SIQ(SWName.MEMORY_MANAGER, SIQ.REQUEST_PAGES, 2));
                intr = receive(SIQ.RESPONSE_PAGES, SIQ.OUT_OF_MEMORY);
                if (intr.id == SIQ.OUT_OF_MEMORY) throw new ProcessLoadException(intr.id);
                // generate process
                Process process = new Process(processIdQueue.dequeue());
                List<Page> pages = (List<Page>) intr.values[0];
                process.setPage(pages.get(0), pages.get(1));
                send(new SIQ(SWName.MEMORY_MANAGER, SIQ.REQUEST_MEMORY_WRITE, pages.get(0).base, file.getRecords()));
                receive(SIQ.RESPONSE_MEMORY_WRITE);
                scheduler.admit(process);
            } catch (ProcessLoadException e) {
            }
        }
    }

}