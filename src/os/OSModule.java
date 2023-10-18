package os;

import os.interrupt_bus.InterruptBus;

public abstract class OSModule implements Runnable{

    protected InterruptBus interruptBus;

    public OSModule(String name) {
        interruptBus.register(name);
    }

}
