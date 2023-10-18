package hardware.io_device;

import common.Component;
import hardware.HWInterrupt;

public abstract class IODevice extends Component<HWInterrupt> implements Runnable {

    public abstract void read(int addr);

    public abstract void write(int addr, long val);

    public abstract void handleInterrupt();

}
