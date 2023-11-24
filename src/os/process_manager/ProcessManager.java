package os.process_manager;

<<<<<<< HEAD
import common.MiniVMUtils;
import cpu.CPU;
import cpu.CPUContext;
import os.compiler.Compiler;
import os.file_system.FileSystem;
import os.memory_manager.MemoryManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessManager {
    // attributes
    private static final String INIT_PROCESS_PATH = "/src/os/process_manager/init_process";
    private static final int DEFAULT_DATA_SIZE = 16;
    private static final int DEFAULT_STACK_SIZE = 64;
    private static final int DEFAULT_HEAP_SIZE = 64;
    // associations
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    // hardware
    private final CPU cpu;
    // components
    private Process runningProcess = null;
    private final Queue<Process> readyQueue = new LinkedList<>();

    public ProcessManager(CPU cpu) {
        // set associations
        this.cpu = cpu;
    }

    public void associate(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public void associate(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String createInitProcess() {
        Process process = new Process(0, "init_process");
        process.setCode(0, 1);
        process.setData(process.codeBase + process.codeSize, DEFAULT_DATA_SIZE);
        process.setStack(process.dataBase + process.dataSize, DEFAULT_STACK_SIZE);
        process.setHeap(process.stackBase + process.stackSize, DEFAULT_HEAP_SIZE);
        if ((process.pageTable = memoryManager.allocate(1)) == null) return "Page fault.";
        memoryManager.write(process.pageTable.get(0), 0, 469762048); // 0 00111 00 000000000000 000000000000
        runningProcess = process;
        return "Process " + "init_process" + " has been created.";
    }

    public String createProcess(String programName) {
        List<Byte> program = fileSystem.readContents(programName);
        List<Integer> machineCodes = Compiler.compile(program);
        if (machineCodes == null) return "Compile error.";
        Process process = new Process(0, programName);
        process.setCode(0, machineCodes.size());
        process.setData(process.codeBase + process.codeSize, DEFAULT_DATA_SIZE);
        process.setStack(process.dataBase + process.dataSize, DEFAULT_STACK_SIZE);
        process.setHeap(process.stackBase + process.stackSize, DEFAULT_HEAP_SIZE);
        if ((process.pageTable = memoryManager.allocate(process.size())) == null) return "Page fault.";
        for (int codeIndex = 0; codeIndex < machineCodes.size(); codeIndex++)
            memoryManager.write(process.pageTable.get(codeIndex >> 6), codeIndex & 63, machineCodes.get(codeIndex));
        if (runningProcess == null) runningProcess = process;
        else readyQueue.add(process);
        return "Process " + programName + " has been created.";
    }

    public void switchContext() {
        if (runningProcess == null) return;
        runningProcess.setCPUContext(cpu.getContext());
        readyQueue.add(runningProcess);
        runningProcess = readyQueue.poll();
        cpu.setContext(runningProcess.getCPUContext());
    }

    public void terminateProcess() {
        if (runningProcess == null) return;
        memoryManager.free(runningProcess.pageTable);
        runningProcess = readyQueue.poll();
        cpu.setContext(runningProcess.getCPUContext());
    }

    public Process getRunningProcess() {
        return runningProcess;
    }

    public CPUContext getCPUContext() {
        return cpu.getContext();
    }

}
=======
import hardware.HIRQ;
import hardware.HWName;
import interrupt.CircularQueue;
import interrupt.InterruptServiceRoutine;
import interrupt.Utils;
import interrupt.logger.MiniVMLogger;
import os.OSModule;
import os.SIRQ;
import os.SWName;
import os.file_manager.Executable;
import os.file_manager.File;
import os.file_manager.FileType;
import os.memory_manager.Page;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProcessManager extends OSModule {
    // attributes
    private int loadedProcess = 0;
    private static final int MAX_CONCURRENT_PROCESS = 4;
    // hardware
    private CPU cpu;
    // components
    private final Scheduler scheduler = new Scheduler();
    private final Loader loader = new Loader();
    private CircularQueue<Integer> processIdQueue;

    public ProcessManager() {
        createProcessIdQueue();
        registerISR(SIRQ.REQUEST_LOAD_PROCESS, loader::loadProcess);
        registerISR(SIRQ.REQUEST_SWITCH_CONTEXT, scheduler::switchContext);
        registerISR(SIRQ.REQUEST_TERMINATE_PROCESS, scheduler::terminate);
        registerISR(SIRQ.REQUEST_IO_WRITE, scheduler::ioWrite);
        registerISR(SIRQ.COMPLETE_IO, scheduler::ioComplete);
        registerISR(SIRQ.REQUEST_KILL_PROCESS, scheduler::killProcess);
    }

    @Override
    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    private void createProcessIdQueue() {
        processIdQueue = new CircularQueue<>(MAX_CONCURRENT_PROCESS);
        for (int processId = 0; processId < processIdQueue.capacity(); processId++)
            processIdQueue.enqueue(processId);
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
        public void killProcess(SIRQ intr) {
            if (runningProcess == null) return;
            cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.HALT));
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
            if (runningProcess == null) {
                cpu.generateIntr(new HIRQ(HWName.CPU, HIRQ.RESPONSE_TERMINATE));
                return;
            }
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
            --loadedProcess;
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
            if (loadedProcess == MAX_CONCURRENT_PROCESS) {
                MiniVMLogger.error("ProcessManager", "Max concurrent processes have been loaded.");
                return;
            }
            // resource allocation
            String fileName = (String) intr.values()[0];
            send(new SIRQ(SWName.FILE_MANAGER, SIRQ.REQUEST_FILE, fileName));
            intr = receive(SIRQ.RESPONSE_FILE, SIRQ.FILE_NOT_FOUND);
            if (intr.id() == SIRQ.FILE_NOT_FOUND) {
                MiniVMLogger.error("ProcessManager", "File not found.");
                return;
            }
            File file = (File) intr.values()[0];
            if (file.type != FileType.EXECUTABLE) {
                MiniVMLogger.error("ProcessManager", fileName + " is not executable.");
                return;
            }
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_PAGES, 2));
            intr = receive(SIRQ.RESPONSE_PAGES, SIRQ.OUT_OF_MEMORY);
            if (intr.id() == SIRQ.OUT_OF_MEMORY) return;
            // generate process
            List<Page> pages = (List<Page>) intr.values()[0];
            Process process = new Process(processIdQueue.dequeue(), pages.get(0), pages.get(1));
            Executable exe = new Executable(file);
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_MEMORY_WRITE, pages.get(0).base(), exe.getInstructions()));
            receive(SIRQ.RESPONSE_MEMORY_WRITE);
            send(new SIRQ(SWName.MEMORY_MANAGER, SIRQ.REQUEST_MEMORY_WRITE, pages.get(1).base(), exe.getData()));
            receive(SIRQ.RESPONSE_MEMORY_WRITE);
            scheduler.admit(process);
            ++loadedProcess;
        }
    }

    @Override
    public String toString() {
        return "[Process Manager]\n"
                + "Running process      : " + scheduler.runningProcess + "\n"
                + "Ready queue          : " + scheduler.readyQueue + "\n"
                + "Block queue          : " + Utils.list(scheduler.blockQueue) + "\n"
                + cpu.save() + "\n";
    }

}
>>>>>>> origin/master
