package os;

import interrupt.bus.Bus;
import interrupt.bus.Component;
import hardware.Memory;
import hardware.io_device.IODevice;
import hardware.storage.Storage;
import os.file_manager.FileManager;
import os.io_manager.IOManager;
import os.memory_manager.MemoryManager;

public class OS extends Component<SIRQ> implements SystemCall {

    private final OSModule processManager = new ProcessManager();
    private final OSModule memoryManager = new MemoryManager();
    private final OSModule fileManager = new FileManager();
    private final OSModule ioManager = new IOManager();

    public OS() {
        Bus<SIRQ> intrBus = new Bus<>();
        associate(intrBus, SWName.OS);
        processManager.associate(intrBus, SWName.PROCESS_MANAGER);
        memoryManager.associate(intrBus, SWName.MEMORY_MANAGER);
        fileManager.associate(intrBus, SWName.FILE_MANAGER);
        ioManager.associate(intrBus, SWName.IO_MANAGER);
    }

    @Override
    public void generateIntr(SIRQ intr) {
        send(intr);
    }

    @Override
    public String status(String moduleName) {
        String status = "";
        switch (moduleName) {
            case SWName.PROCESS_MANAGER -> status = processManager.toString();
            case SWName.MEMORY_MANAGER -> status = memoryManager.toString();
            case SWName.FILE_MANAGER -> status = fileManager.toString();
            case SWName.IO_MANAGER -> status = ioManager.toString();
        }
        return status;
    }

    @Override
    public void associate(CPU cpu) {
        processManager.associate(cpu);
    }

    @Override
    public void associate(IODevice device) {
        if (device instanceof Memory memory) {
            processManager.associate(memory);
            memoryManager.associate(memory);
        } else if (device instanceof Storage storage) fileManager.associate(storage);
        else ioManager.associate(device);
    }

    @Override
    public void run() {
        new Thread(processManager).start();
        new Thread(memoryManager).start();
        new Thread(fileManager).start();
        new Thread(ioManager).start();
    }

}
