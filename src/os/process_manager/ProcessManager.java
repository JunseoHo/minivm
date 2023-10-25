package os.process_manager;

import common.CircularQueue;
import common.InterruptServiceRoutine;
import hardware.HIRQ;
import hardware.HWName;
import hardware.cpu.CPU;
import os.OSModule;
import os.SIRQ;
import os.SWName;
import os.file_manager.File;
import os.file_manager.FileType;
import os.memory_manager.Page;

import java.util.*;

public class ProcessManager extends OSModule {
    // attributes
    private static final int MAX_CONCURRENT_PROCESS = 5;
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
        registerInterruptServiceRoutine(SIRQ.REQUEST_LOAD_PROCESS, (intr) -> loader.loadProcess(intr));
        registerInterruptServiceRoutine(SIRQ.REQUEST_SWITCH_CONTEXT, (intr) -> scheduler.switchContext(intr));
        registerInterruptServiceRoutine(SIRQ.REQUEST_TERMINATE_PROCESS, (intr) -> scheduler.terminate(intr));
        registerInterruptServiceRoutine(SIRQ.REQUEST_IO_WRITE, (intr) -> scheduler.ioWrite(intr));
        registerInterruptServiceRoutine(SIRQ.COMPLETE_IO, (intr) -> scheduler.ioComplete(intr));
    }

    @Override
    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    private class Scheduler {
        private Process runningProcess;
        private final CircularQueue<Process> readyQueue = new CircularQueue<>();
        private final List<Process> blockQueue = new LinkedList<>();

        public void admit(Process process) {
            if (runningProcess == null) {
                runningProcess = process;
                cpu.restore(process.save());
            } else readyQueue.enqueue(process);
        }

        @InterruptServiceRoutine
        public void ioWrite(SIRQ intr) {
            int runningProcessId = runningProcess.getId();
            int port = ((int) intr.values()[0]);
            int base = ((int) intr.values()[1]);
            int size = ((int) intr.values()[2]);
            if (runningProcess == null) return;
            runningProcess.restore(cpu.save());
            blockQueue.add(runningProcess);
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.switchStatus(true);
            } else {
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            send(new SIRQ(SWName.IO_MANAGER, SIRQ.REQUEST_IO_WRITE, runningProcessId, port, base, size));
            receive(SIRQ.RESPONSE_IO_WRITE);
            cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.RESPONSE_WRITE));
        }

        @InterruptServiceRoutine
        public void ioComplete(SIRQ intr) {
            int processId = (int) intr.values()[0];
            for (int index = 0; index < blockQueue.size(); index++) {
                Process process = blockQueue.get(index);
                if (process.getId() == processId) {
                    blockQueue.remove(index);
                    admit(process);
                    break;
                }
            }
            cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.RESPONSE_TERMINATE));
        }

        public void terminate(SIRQ intr) {
            processIdQueue.enqueue(runningProcess.getId());
            List<Page> pages = new ArrayList<>();
            pages.add(runningProcess.getCodeSegment());
            pages.add(runningProcess.getDataSegment());
            if (readyQueue.isEmpty()) {
                runningProcess = null;
                cpu.switchStatus(true);
            } else {
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_FREE_PAGE, pages));
            receive(SIRQ.RESPONSE_FREE_PAGE);
            cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.RESPONSE_TERMINATE));
        }

        public void switchContext(SIRQ intr) {
            if (runningProcess != null && !readyQueue.isEmpty()) {
                runningProcess.restore(cpu.save());
                readyQueue.enqueue(runningProcess);
                runningProcess = readyQueue.dequeue();
                cpu.restore(runningProcess.save());
            }
            cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.RESPONSE_TERMINATE));
        }

    }

    private class Loader {

        public void loadProcess(SIRQ intr) {
            // resource allocation
            String fileName = (String) intr.values()[0];
            send(new SIRQ(SWName.FILE_MANAGER, SIRQ.REQUEST_FILE, fileName));
            intr = receive(SIRQ.RESPONSE_FILE, SIRQ.FILE_NOT_FOUND);
            if (intr.id() == SIRQ.FILE_NOT_FOUND) return;
            File file = (File) intr.values()[0];
            if (file.type != FileType.EXECUTABLE) return;
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_PAGES, 2));
            intr = receive(SIRQ.RESPONSE_PAGES, SIRQ.OUT_OF_MEMORY);
            if (intr.id() == SIRQ.OUT_OF_MEMORY) return;
            // generate process
            List<Page> pages = (List<Page>) intr.values()[0];
            Process process = new Process(processIdQueue.dequeue(), pages.get(0), pages.get(1));
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_MEMORY_WRITE, pages.get(0).base(), file.getRecords()));
            receive(SIRQ.RESPONSE_MEMORY_WRITE);
            scheduler.admit(process);
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