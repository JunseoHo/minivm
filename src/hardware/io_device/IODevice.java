package hardware.io_device;

import hardware.ControlBus;

public abstract class IODevice implements Runnable {

    private ControlBus controlBus;

    public void associate(ControlBus controlBus) {
        this.controlBus = controlBus;
    }

    protected void sendInterrupt(int interruptId) {
        controlBus.send(interruptId);
    }

    public abstract long read(int addr);

    public abstract void write(int addr, long val);

}
