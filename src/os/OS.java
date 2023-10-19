package os;

import common.bus.Bus;
import hardware.cpu.CPU;
import hardware.Memory;
import hardware.io_device.IODevice;
import hardware.storage.Storage;
import os.file_manager.File;
import os.file_manager.FileManager;
import os.file_manager.FileType;
import os.io_manager.IOManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

public class OS implements SystemCall {

    private Bus interruptBus;
    private OSModule processManager;
    private OSModule memoryManager;
    private OSModule fileManager;
    private OSModule ioManager;

    public OS() {
        // create OS modules
        interruptBus = new Bus();
        processManager = new ProcessManager();
        memoryManager = new MemoryManager();
        fileManager = new FileManager();
        ioManager = new IOManager();
        // associate interrupt bus with OS modules
        processManager.associate(interruptBus, "ProcessManager");
        memoryManager.associate(interruptBus, "MemoryManager");
        fileManager.associate(interruptBus, "FileManager");
        ioManager.associate(interruptBus, "IOManager");
    }

    @Override
    public void associate(CPU cpu) {
        processManager.associate(cpu);
    }

    @Override
    public void associate(IODevice ioDevice) {
        if (ioDevice instanceof Memory memory) {
            processManager.associate(memory);
            memoryManager.associate(memory);
        } else if (ioDevice instanceof Storage storage) fileManager.associate(storage);
        else ioManager.associate(ioDevice);
    }

    @Override
    public void run() {
        // start
        System.out.println("OS start.");
        new Thread(processManager).start();
        new Thread(memoryManager).start();
        new Thread(fileManager).start();
        new Thread(ioManager).start();
        File file = new File(FileType.EXECUTABLE);
        file.addRecord(67108865);
        file.addRecord(67108866);
        file.addRecord(67108867);
        file.addRecord(67108868);
        file.addRecord(67108869);
        file.addRecord(402653185);
        ProcessManager pm = (ProcessManager) processManager;
        pm.newProcess(file);
        pm.newProcess(file);
        pm.newProcess(file);
    }

    @Override
    public void switchContext() {
        ProcessManager processManager = (ProcessManager) this.processManager;
        processManager.switchContext();
    }
}
