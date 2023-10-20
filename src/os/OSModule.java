package os;

import common.CircularQueue;
import common.bus.Component;
import hardware.cpu.CPU;
import hardware.io_device.IODevice;

public abstract class OSModule extends Component<SIQ> implements Runnable {

    protected SIQ interrupt;
    protected CircularQueue<SIQ> interruptQueue = new CircularQueue<>(100);

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public abstract void handleInterrupt();

}
