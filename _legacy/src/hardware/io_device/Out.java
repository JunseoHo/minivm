package hardware.io_device;

import interrupt.Utils;
import hardware.HIRQ;
import hardware.HWName;

public class Out extends IODevice {

    public Out() {
        super();
        init();
    }

    public Out(int bufferSize) {
        super(bufferSize);
        init();
    }

    @Override
    protected void init() {
        registerISR(HIRQ.REQUEST_WRITE, this::write);
    }

    @Override
    public void read(HIRQ intr) {
        /* DO NOTHING */
    }

    @Override
    public void write(HIRQ intr) {
        flush();
        int processId = (int) intr.values()[0];
        int base = (int) intr.values()[1];
        int size = (int) intr.values()[2];
        int addr = 0;
        for (int index = 0; index < size; index++) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, base + index, name()));
            intr = receive(HIRQ.RESPONSE_READ);
            long value = (long) intr.values()[0];
            writeBuffer(addr++, (char) value);
            Utils.sleep(1000); // down clock
        }
        send(new HIRQ(HWName.CPU, HIRQ.COMPLETE_IO, processId));
    }

    @Override
    public String toString() {
        String str = name();
        str += " -> ";
        for (int addr = 0; addr < bufferSize(); addr++) {
            char c = (char) readBuffer(addr);
            if (c == '\0') break;
            else str += c;
        }
        return str;
    }
}
