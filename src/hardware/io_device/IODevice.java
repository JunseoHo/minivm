package hardware.io_device;

import common.bus.Component;
import hardware.HIQ;

public abstract class IODevice extends Component<HIQ> implements Runnable {

    public abstract void read(int addr);

    public abstract void write(int addr, long val);

    public abstract void handleInterrupt();

    public void generateInterrupt(HIQ intr) {
        queue.enqueue(intr);
    }
}
