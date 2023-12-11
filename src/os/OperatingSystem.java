package os;

import common.bus.Bus;
import common.bus.Component;
import common.bus.SWInterrupt;
import hardware.cpu.CPU;
import hardware.disk.Disk;
import hardware.ram.RAM;
import os.file_system.FileSystem;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

import java.util.List;

public class OperatingSystem extends Component<SWInterrupt> {

    public final ProcessManager processManager;
    public final MemoryManager memoryManager;
    public final FileSystem fileSystem;

    public OperatingSystem(CPU cpu, RAM ram, Disk disk) {
        processManager = new ProcessManager(cpu);
        memoryManager = new MemoryManager(ram);
        fileSystem = new FileSystem(disk);
        processManager.associate(memoryManager);
        processManager.associate(fileSystem);
        Bus<SWInterrupt> interruptBus = new Bus<>();
        processManager.associate(interruptBus, SWInterrupt.PM);
        memoryManager.associate(interruptBus, SWInterrupt.MM);
        new Thread(processManager).start();
        new Thread(memoryManager).start();
        processManager.createInitProcess();
    }

    public int readMemory(int logicalAddr) {
        int pageNumber = logicalAddr >> 6;
        int displacement = logicalAddr & 63;
        int pageIndex = processManager.getRunningProcess().pageTable.get(pageNumber);
        return memoryManager.read(pageIndex, displacement);
    }

    public void writeMemory(int logicalAddr, int val) {
        int pageNumber = logicalAddr >> 6;
        int displacement = logicalAddr & 63;
        int pageIndex = processManager.getRunningProcess().pageTable.get(pageNumber);
        memoryManager.write(pageIndex, displacement, val);
    }

    public String createProcess(String fileName) {
        return processManager.createProcess(fileName);
    }

    public int malloc(int size) {
       return processManager.allocate(size);
    }

    public void free(int logicalAddr) {
        processManager.free(logicalAddr);
    }

    public void terminate() {
        processManager.terminateProcess();
    }

    public void switchContext() {
        processManager.switchContext();
    }

    public String mkdir(String name) {
        return fileSystem.mkdir(name);
    }

    public String rmdir(String name) {
        return fileSystem.rmdir(name);
    }

    public String ls() {
        return fileSystem.ls();
    }

    public String touch(String name) {
        return fileSystem.touch(name);
    }

    public String rm(String name) {
        return fileSystem.rm(name);
    }

    public String cd(String name) {
        return fileSystem.cd(name);
    }

    public String save() {
        return fileSystem.saveDiskImage();
    }

}
