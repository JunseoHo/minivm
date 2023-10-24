package os;

import hardware.cpu.CPU;
import hardware.io_device.IODevice;

public interface SystemCall {

    void run();

    void associate(CPU cpu);

    void associate(IODevice device);

    void generateIntr(SIRQ intr);

    String status(String moduleName);

}
