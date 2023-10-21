package os.io_manager;

import hardware.io_device.IODevice;

import java.util.Vector;

public class IODeviceVector {

    private Vector<IODevice> ioDeviceVector;

    public IODeviceVector() {
        ioDeviceVector = new Vector<>();
    }

    public void register(IODevice ioDevice) {
        ioDeviceVector.add(ioDevice);
    }

    public IODevice get(int port) {
        return ioDeviceVector.get(port);
    }

    @Override
    public String toString() {
        String str = "";
        for (int port = 0; port < ioDeviceVector.size(); port++)
            str += "PORT " + port + " : " + ioDeviceVector.get(port) + "\n";
        return str;
    }

}
