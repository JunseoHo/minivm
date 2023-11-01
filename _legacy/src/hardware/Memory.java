package hardware;

import hardware.io_device.IODevice;

public class Memory extends IODevice {

    public Memory() {
        super();
        init();
    }

    public Memory(int size) {
        super(size);
        init();
    }

    @Override
    protected void init() {
        registerISR(HIRQ.REQUEST_READ, this::read);
        registerISR(HIRQ.REQUEST_WRITE, this::write);
    }

}
