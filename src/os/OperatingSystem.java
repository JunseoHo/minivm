package os;

import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.process_manager.ProcessManager;

public class OperatingSystem {

    private ProcessManager processManager;
    private MemoryManager memoryManager;
    private FileManager fileManager;

    public OperatingSystem() {
        this.processManager = new ProcessManager();
        this.memoryManager = new MemoryManager();
        this.fileManager = new FileManager();
    }

    public void run() {

    }

}
