package hardware.io_device;

import hardware.HIQ;
import hardware.HWName;

import java.util.ArrayList;
import java.util.List;

public class StandardOutput extends IODevice {

    public List<Character> buffer;

    public StandardOutput() {
        buffer = new ArrayList<>();
    }

    @Override
    public void read(int addr) {
    }

    @Override
    public void write(int addr, long val) {

    }

    public void write(HIQ intr) {
        buffer.clear();
        int processId = (int) intr.values[0];
        int base = (int) intr.values[1];
        int size = (int) intr.values[2];
        for (int index = 0; index < size; index++) {
            send(new HIQ(HWName.MEMORY, HIQ.REQUEST_IO_READ, base + index, name));
            intr = receive(HIQ.RESPONSE_IO_READ);
            long value = (long) intr.values[0];
            buffer.add((char) value);
        }
        send(new HIQ(HWName.CPU, HIQ.COMPLETE_IO, processId));
    }

    @Override
    public void handleInterrupt() {
        if (queue.isEmpty()) return;
        HIQ intr = queue.dequeue();
        if (intr == null) return;
        switch (intr.id) {
            case HIQ.REQUEST_IO_WRITE -> write(intr);
        }
    }

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    @Override
    public String toString() {
        String str = "Stdout";
        if (!buffer.isEmpty()) {
            str += " -> ";
            for (char c : buffer) str += c;
        }
        return str;
    }
}
