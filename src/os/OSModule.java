package os;

import common.CircularQueue;
import common.bus.Component;
import hardware.CPU;
import hardware.io_device.IODevice;

public abstract class OSModule extends Component<SWInterrupt> implements Runnable {

    protected SWInterrupt interrupt;
    protected CircularQueue<SWInterrupt> interruptQueue = new CircularQueue<>(100);

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public SWInterrupt receive(int... interruptIds) {
        while (true) {
            interrupt = receive();
            for (int interruptId : interruptIds) if (interrupt.id == interruptId) return interrupt;
            interruptQueue.enqueue(interrupt);
        }
    }

    public abstract void handleInterrupt();

}
