package os;

import common.Component;
import hardware.CPU;
import hardware.io_device.IODevice;

public abstract class OSModule extends Component<SWInterrupt> implements Runnable {

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public abstract void handleInterrupt();

}
