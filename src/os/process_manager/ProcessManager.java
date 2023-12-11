package os.process_manager;

import common.SyncQueue;
import common.bus.SWInterrupt;
import hardware.cpu.CPU;
import hardware.cpu.CPUContext;
import os.OSModule;
import os.compiler.Compiler;
import os.file_system.FileSystem;
import os.memory_manager.MemoryManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ProcessManager extends OSModule {
    // attributes
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
    private boolean[] processIdTable = new boolean[100];
    private final SyncQueue<Process> readyQueue = new SyncQueue<>();
    private final SyncQueue<Process> blockQueue = new SyncQueue<>();

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
        send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_ALLOCATE_MEMORY, 1));
        SWInterrupt intr = receive(SWInterrupt.RESPONSE_ALLOCATE_MEMORY, SWInterrupt.OUT_OF_MEMORY);
        if (intr.id() == SWInterrupt.OUT_OF_MEMORY) return "Page fault.";
        process.pageTable = (List<Integer>) intr.values()[0];
        send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_WRITE_MEMORY, process.pageTable.get(0), 0, 469762048));
        receive(SWInterrupt.RESPONSE_WRITE_MEMORY);
        runningProcess = process;
        return "Process " + "init_process" + " has been created.";
    }

    public String createProcess(String programName) {
        List<Byte> program = fileSystem.readContents(programName);
        if (program == null) return "File not found.";
        List<Integer> machineCodes = Compiler.compile(program);
        if (machineCodes == null) return "Compile error.";
        Process process = new Process(getProcessId(), programName);
        process.setCode(0, machineCodes.size());
        process.setData(process.codeBase + process.codeSize, DEFAULT_DATA_SIZE);
        process.setStack(process.dataBase + process.dataSize, DEFAULT_STACK_SIZE);
        process.setHeap(process.stackBase + process.stackSize, DEFAULT_HEAP_SIZE);
        send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_ALLOCATE_MEMORY, process.size()));
        SWInterrupt intr = receive(SWInterrupt.RESPONSE_ALLOCATE_MEMORY, SWInterrupt.OUT_OF_MEMORY);
        if (intr.id() == SWInterrupt.OUT_OF_MEMORY) return "Page fault.";
        process.pageTable = (List<Integer>) intr.values()[0];

        for (int codeIndex = 0; codeIndex < machineCodes.size(); codeIndex++) {
            send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_WRITE_MEMORY,
                    process.pageTable.get(codeIndex >> 6), codeIndex & 63, machineCodes.get(codeIndex)));
            receive(SWInterrupt.RESPONSE_WRITE_MEMORY);
        }
        for (int heapIndex = 0; heapIndex < process.heapSize; heapIndex++) {
            send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_WRITE_MEMORY,
                    process.pageTable.get((heapIndex + process.heapBase) >> 6), (heapIndex + process.heapBase) & 63, null));
            receive(SWInterrupt.RESPONSE_WRITE_MEMORY);
        }
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
        freeProcessId(runningProcess.id);
        send(new SWInterrupt(SWInterrupt.MM, SWInterrupt.REQUEST_FREE_MEMORY, runningProcess.pageTable));
        receive(SWInterrupt.RESPONSE_FREE_MEMORY);
        runningProcess = readyQueue.poll();
        cpu.setContext(runningProcess.getCPUContext());
    }

    public Process getRunningProcess() {
        return runningProcess;
    }

    public CPUContext getCPUContext() {
        return cpu.getContext();
    }

    public SyncQueue<Process> getReadyQueue() {
        return readyQueue;
    }

    public SyncQueue<Process> getBlockQueue() {
        return blockQueue;
    }

    public int allocate(int size) {
        Map<Integer, Integer> objTable = runningProcess.objectTable;
        int left = runningProcess.heapBase, right = runningProcess.heapBase;
        while (left < runningProcess.heapSize + runningProcess.heapBase && right < runningProcess.heapSize + runningProcess.heapBase) {
            if (objTable.get(right) == null) {
                if (right - left + 1 == size) {
                    runningProcess.addObject(left, size);
                    return left;
                } else ++right;
            } else {
                left = objTable.get(right) + right;
                right = left;
            }
        }
        return -1;
    }

    public void free(int base) {
        runningProcess.removeObject(base);
    }

    public int getProcessId() {
        for (int i = 0; i < processIdTable.length; i++) {
            if (!processIdTable[i]) {
                processIdTable[i] = true;
                return i + 10000;
            }
        }
        return -1;
    }

    public void freeProcessId(int i) {
        processIdTable[i - 10000] = false;
    }

    @Override
    public void run() {

    }

}