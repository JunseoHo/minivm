package os;

import hardware.cpu.CPU;
import hardware.io_device.IODevice;

public interface SystemCall {

    void associate(CPU cpu);

    void associate(IODevice ioDevice);

    void run();

    void switchContext();
}
