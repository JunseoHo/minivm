package os;

import os.interrupt_bus.InterruptBus;

public class OS implements SystemCall {

    private InterruptBus interruptBus;
    private OSModule processManager;
    private OSModule memoryManager;
    private OSModule fileManager;
    private OSModule ioManager;




}
