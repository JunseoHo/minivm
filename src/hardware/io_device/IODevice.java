package hardware.io_device;

import common.Bus;
import common.Component;
import hardware.interrupt.IOInterrupt;

public abstract class IODevice extends Component<IOInterrupt> implements Runnable {

    public abstract long read(int addr);

    public abstract void write(int addr, long val);

    public abstract void handleInterrupt();

}
