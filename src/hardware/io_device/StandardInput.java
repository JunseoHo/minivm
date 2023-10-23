package hardware.io_device;

import hardware.HIRQ;

public class StandardInput extends IODevice {

    @Override
    public String toString(){
        return "Stdin";
    }

    @Override
    public void read(HIRQ intr) {

    }

    @Override
    public void write(HIRQ intr) {

    }

    @Override
    public int bufferSize() {
        return 0;
    }
}
