package operating_system.process_manager;

import hardware.CPU;
import operating_system.OperatingSystem;
import operating_system.memory_manager.Memory;

public class ProcessManager {

    private CPU cpu;
    private Memory memory;
    private OperatingSystem.InterruptHandler interruptHandler;

}
