package hardware.io_device;

import hardware.HIRQ;
import hardware.HWName;

import java.util.ArrayList;
import java.util.List;

public class StandardOutput extends IODevice {

    public List<Character> buffer;

    public StandardOutput() {
        buffer = new ArrayList<>();
        registerInterruptServiceRoutine(HIRQ.REQUEST_IO_WRITE, this::write);
    }

    @Override
    public void read(HIRQ intr) {
        /* DO NOTHING */
    }
    @Override
    public void write(HIRQ intr) {
        buffer.clear();
        int processId = (int) intr.values()[0];
        int base = (int) intr.values()[1];
        int size = (int) intr.values()[2];
        for (int index = 0; index < size; index++) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_IO_READ, base + index, name()));
            intr = receive(HIRQ.RESPONSE_IO_READ);
            long value = (long) intr.values()[0];
            buffer.add((char) value);
        }
        send(new HIRQ(HWName.CPU, HIRQ.COMPLETE_IO, processId));
    }

    @Override
    public int bufferSize() {
        return buffer.size();
    }

    @Override
    public String toString() {
        String str = name();
        if (!buffer.isEmpty()) {
            str += " -> ";
            for (char c : buffer) str += c;
        }
        return str;
    }
}
