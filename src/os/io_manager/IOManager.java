package os.io_manager;

import hardware.io_device.IODevice;
import os.OSModule;

public class IOManager extends OSModule {

    private IODeviceVector ioDeviceVector;

    public IOManager() {
        ioDeviceVector = new IODeviceVector();
    }

    @Override
    public void associate(IODevice ioDevice) {
        ioDeviceVector.register(ioDevice);
    }

    @Override
    public String toString() {
        return "[IO manager]\n" + ioDeviceVector.toString();
    }
}
