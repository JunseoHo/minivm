package hardware;

import hardware.io_device.IODevice;

public class Memory extends IODevice {
    // constants
    private static final int MIN_SIZE = 128;
    private static final int MAX_SIZE = 8192;
    private static final int DEFAULT_SIZE = 1024;
    // working variables
    private int size;
    private long[] memory;

    public Memory() {
        this(DEFAULT_SIZE);
    }

    public Memory(int size) {
        // create memory
        if (size < MIN_SIZE || size > MAX_SIZE) size = DEFAULT_SIZE;
        memory = new long[this.size = size];
        // register interrupt service routines
        registerInterruptServiceRoutine(HIRQ.REQUEST_READ, this::read);
        registerInterruptServiceRoutine(HIRQ.REQUEST_WRITE, this::write);
    }

    @Override
    public synchronized void read(HIRQ intr) {
        int addr = (int) intr.values()[0];
        String receiver = (String) intr.values()[1];
        if (addr < 0 || addr > size - 1) send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        else send(new HIRQ(receiver, HIRQ.RESPONSE_READ, memory[addr]));
    }

    @Override
    public synchronized void write(HIRQ intr) {
        int addr = (int) intr.values()[0];
        long val = (long) intr.values()[1];
        String receiver = (String) intr.values()[2];
        if (addr < 0 || addr > size - 1) send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        else send(new HIRQ(receiver, HIRQ.RESPONSE_WRITE, memory[addr] = val));
    }

    @Override
    public int bufferSize() {
        return size;
    }

}
