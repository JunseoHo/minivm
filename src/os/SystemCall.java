package os;

import hardware.cpu.CPU;
import hardware.io_device.IODevice;
import os.file_manager.File;

public interface SystemCall {

    void associate(CPU cpu);

    void associate(IODevice ioDevice);

    void run();

    void switchContext();

    File getCurrentDir();

    File getFile(String parameter);
}
