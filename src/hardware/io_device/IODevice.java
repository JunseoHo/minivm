package hardware.io_device;

import common.bus.Component;
import common.logger.MiniVMLogger;
import hardware.HIRQ;
import hardware.HW_ISR;
import main.MiniVM;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class IODevice extends Component<HIRQ> implements Runnable {

    private static final int MIN_SIZE = 128;
    private static final int MAX_SIZE = 8192;
    private static final int DEFAULT_SIZE = 1024;
    private final Map<Integer, HW_ISR> interruptVectorTable = new HashMap<>();
    private final long[] buffer;
    private final int bufferSize;

    public IODevice() {
        this(DEFAULT_SIZE);
    }

    public IODevice(int bufferSize) {
        if (bufferSize < MIN_SIZE || bufferSize > MAX_SIZE) {
            MiniVMLogger.warn("IODevice", "Invalid buffer size.");
            bufferSize = DEFAULT_SIZE;
        }
        buffer = new long[this.bufferSize = bufferSize];
    }

    protected abstract void init();

    protected void registerISR(int interruptId, HW_ISR routine) {
        interruptVectorTable.put(interruptId, routine);
    }

    protected synchronized void read(HIRQ intr) {
        int addr = intr.values()[0] instanceof Integer ? (int) intr.values()[0] : ((Long) intr.values()[0]).intValue();
        String receiver = (String) intr.values()[1];
        if (addr < 0 || addr > bufferSize() - 1) {
            MiniVMLogger.error(name(), "Segmentation fault.");
            send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        } else send(new HIRQ(receiver, HIRQ.RESPONSE_READ, readBuffer(addr)));
    }

    protected synchronized void write(HIRQ intr) {
        int addr = intr.values()[0] instanceof Integer ? (int) intr.values()[0] : ((Long) intr.values()[0]).intValue();
        long val = (long) intr.values()[1];
        String receiver = (String) intr.values()[2];
        if (addr < 0 || addr > bufferSize() - 1) {
            MiniVMLogger.error(name(), "Segmentation fault.");
            send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        } else send(new HIRQ(receiver, HIRQ.RESPONSE_WRITE, writeBuffer(addr, val)));
    }

    @Override
    public void run() {
        while (true) {
            for (HIRQ intr : receiveAll()) enqueue(intr);
            while (!isEmpty()) {
                HIRQ intr = dequeue();
                HW_ISR routine = interruptVectorTable.get(intr.id());
                if (routine != null) routine.handle(intr);
            }
        }
    }

    public int bufferSize() {
        return bufferSize;
    }

    public long readBuffer(int addr) {
        return buffer[addr];
    }

    public long writeBuffer(int addr, long val) {
        return buffer[addr] = val;
    }

    public void flush() {
        Arrays.fill(buffer, 0);
    }

    public void generateIntr(HIRQ intr) {
        enqueue(intr);
    }

}
