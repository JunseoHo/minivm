package os.process_manager;

import common.CircularQueue;
import common.InterruptServiceRoutine;
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

import java.util.*;

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
        registerInterruptServiceRoutine(SIQ.REQUEST_IO_WRITE, (intr) -> scheduler.ioWrite(intr));
        registerInterruptServiceRoutine(SIQ.COMPLETE_IO, (intr) -> scheduler.ioComplete(intr));
    }

    @Override
    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    private class Scheduler {
        private Process runningProcess;
        private CircularQueue<Process> readyQueue = new CircularQueue<>();
        private List<Process> blockQueue = new LinkedList<>();

        public void admit(Process process) {
            if (runningProcess == null) {
                runningProcess = process;
                cpu.restore(process.save());
            } else readyQueue.enqueue(process);
        }

        @InterruptServiceRoutine
        public void ioWrite(SIQ intr) {
            int runningProcessId = runningProcess.getId();
            int port = ((int) intr.values[0]);
            int base = ((int) intr.values[1]);
            int size = ((int) intr.values[2]);
            if (runningProcess == null) return;
            runningProcess.restore(cpu.save());
            blockQueue.add(runningProcess);
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.switchTasking();
            } else {
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            send(new SIQ(SWName.IO_MANAGER, SIQ.REQUEST_IO_WRITE, runningProcessId, port, base, size));
            receive(SIQ.RESPONSE_IO_WRITE);
            cpu.generateInterrupt(new HIQ(HWName.CPU, HIQ.RESPONSE_IO_WRITE));
        }

        @InterruptServiceRoutine
        public void ioComplete(SIQ intr) {
            int processId = (int) intr.values[0];
            for (int index = 0; index < blockQueue.size(); index++) {
                Process process = blockQueue.get(index);
                if (process.getId() == processId) {
                    blockQueue.remove(index);
                    admit(process);
                    break;
                }
            }
            cpu.generateInterrupt(new HIQ(HWName.CPU, HIQ.RESPONSE_TERMINATE));
        }

        public void terminate(SIQ intr) {
            processIdQueue.enqueue(runningProcess.getId());
            List<Page> pages = new ArrayList<>();
            pages.add(runningProcess.getCodeSegment());
            pages.add(runningProcess.getDataSegment());
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.switchTasking();
            } else {
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            send(new SIQ(SWName.MEMORY_MANAGER, SIQ.REQUEST_FREE_PAGE, pages));
            receive(SIQ.RESPONSE_FREE_PAGE);
            cpu.generateInterrupt(new HIQ(HWName.CPU, HIQ.RESPONSE_TERMINATE));
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

    @Override
    public String toString() {
        return "[Process Manager]\n"
                + "Running process      : " + scheduler.runningProcess + "\n"
                + "Ready queue          : " + scheduler.readyQueue + "\n"
                + "Block queue          : " + scheduler.blockQueue + "\n"
                + cpu.save() + "\n";
    }

}