package hardware.io_device;

import hardware.ControlBus;

public abstract class IODevice {

    private ControlBus controlBus;

    public void associate(ControlBus controlBus) {
        this.controlBus = controlBus;
    }

    protected void sendInterrupt(int interruptId) {
        controlBus.send(interruptId);
    }

}
