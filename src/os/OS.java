package os;

import common.bus.Bus;
import common.bus.Component;
import hardware.cpu.CPU;
import hardware.Memory;
import hardware.io_device.IODevice;
import hardware.storage.Storage;
import os.file_manager.File;
import os.file_manager.FileManager;
import os.io_manager.IOManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

public class OS extends Component<SIQ> implements SystemCall {

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
        associate(interruptBus, SWName.OS);
        processManager.associate(interruptBus, SWName.PROCESS_MANAGER);
        memoryManager.associate(interruptBus, SWName.MEMORY_MANAGER);
        fileManager.associate(interruptBus, SWName.FILE_MANAGER);
        ioManager.associate(interruptBus, SWName.IO_MANAGER);
    }

    public void generateInterrupt(SIQ intr) {
        send(intr);
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
        new Thread(processManager).start();
        new Thread(memoryManager).start();
        new Thread(fileManager).start();
        new Thread(ioManager).start();
    }

}
