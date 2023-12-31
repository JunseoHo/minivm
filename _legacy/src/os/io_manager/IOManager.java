package os.io_manager;

import interrupt.InterruptServiceRoutine;
import hardware.HIRQ;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SIRQ;
import os.SWName;

import java.lang.reflect.InvocationTargetException;

public class IOManager extends OSModule {

    private IODeviceVector ioDeviceVector;

    public IOManager() {
        ioDeviceVector = new IODeviceVector();
        registerISR(SIRQ.REQUEST_IO_WRITE, this::write);
    }

    @InterruptServiceRoutine
    public void write(SIRQ intr) {
        int processId = (int) intr.values()[0];
        int port = (int) intr.values()[1];
        int base = (int) intr.values()[2];
        int size = (int) intr.values()[3];
        ioDeviceVector.get(port).generateIntr(new HIRQ(null, HIRQ.REQUEST_WRITE, processId, base, size));
        send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.RESPONSE_IO_WRITE));
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
