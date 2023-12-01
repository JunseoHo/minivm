package os.process_manager;

import hardware.cpu.CPU;
import hardware.cpu.CPUContext;
import os.compiler.Compiler;
import os.file_system.FileSystem;
import os.memory_manager.MemoryManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProcessManager {
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
    private final Queue<Process> readyQueue = new LinkedList<>();
    private final Queue<Process> blockQueue = new LinkedList<>();

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
        if (program == null) return "File not found.";
        List<Integer> machineCodes = Compiler.compile(program);
        if (machineCodes == null) return "Compile error.";
        Process process = new Process(getProcessId(), programName);
        process.setCode(0, machineCodes.size());
        process.setData(process.codeBase + process.codeSize, DEFAULT_DATA_SIZE);
        process.setStack(process.dataBase + process.dataSize, DEFAULT_STACK_SIZE);
        process.setHeap(process.stackBase + process.stackSize, DEFAULT_HEAP_SIZE);
        if ((process.pageTable = memoryManager.allocate(process.size())) == null) return "Page fault.";
        for (int codeIndex = 0; codeIndex < machineCodes.size(); codeIndex++)
            memoryManager.write(process.pageTable.get(codeIndex >> 6), codeIndex & 63, machineCodes.get(codeIndex));
        for (int heapIndex = 0; heapIndex < process.heapSize; heapIndex++)
            memoryManager.write(process.pageTable.get((heapIndex + process.heapBase) >> 6), (heapIndex + process.heapBase) & 63, null);
        if (runningProcess == null) runningProcess = process;
        else readyQueue.add(process);
        return "Process " + programName + " has been created.";
    }

    public String createProcess(List<Integer> machineCodes) {
        Process process = new Process(0, "Sample");
        process.setCode(0, machineCodes.size());
        process.setData(process.codeBase + process.codeSize, DEFAULT_DATA_SIZE);
        process.setStack(process.dataBase + process.dataSize, DEFAULT_STACK_SIZE);
        process.setHeap(process.stackBase + process.stackSize, DEFAULT_HEAP_SIZE);
        if ((process.pageTable = memoryManager.allocate(process.size())) == null) return "Page fault.";
        for (int codeIndex = 0; codeIndex < machineCodes.size(); codeIndex++)
            memoryManager.write(process.pageTable.get(codeIndex >> 6), codeIndex & 63, machineCodes.get(codeIndex));
        if (runningProcess == null) runningProcess = process;
        else readyQueue.add(process);
        return "Process " + "Sample" + " has been created.";
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

    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public Queue<Process> getBlockQueue() {
        return blockQueue;
    }

    public void allocate(int base, int size) {
        runningProcess.addObject(base, size);
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

}