package hardware;

import hardware.io_device.IODevice;

public class Memory extends IODevice {

    private static final int MIN_SIZE = 128;
    private static final int MAX_SIZE = 8192;
    private static final int DEFAULT_SIZE = 1024;
    private int size;
    private long[] memory;

    public Memory() {
        this(DEFAULT_SIZE);
    }

    public Memory(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) size = DEFAULT_SIZE;
        memory = new long[this.size = size];
    }

    @Override
    public synchronized void read(int addr) {
        if (addr < 0 || addr > size - 1) send(new HIQ(HWName.CPU, HIQ.SEGFAULT));
        else send(new HIQ(HWName.CPU, HIQ.RESPONSE_READ, memory[addr]));
    }

    public synchronized void read(int addr, String receiver) {
        if (addr < 0 || addr > size - 1) send(new HIQ(HWName.CPU, HIQ.SEGFAULT));
        else send(new HIQ(receiver, HIQ.RESPONSE_IO_READ, memory[addr]));
    }

    @Override
    public synchronized void write(int addr, long val) {
        if (addr < 0 || addr > size - 1) send(new HIQ(HWName.CPU, HIQ.SEGFAULT));
        else send(new HIQ(HWName.CPU, HIQ.RESPONSE_WRITE, memory[addr] = val));
    }

    @Override
    public void handleInterrupt() {
        HIQ intr;
        if ((intr = receive()) != null) {
            switch (intr.id) {
                case HIQ.STAT_CHK -> send(new HIQ(HWName.CPU, HIQ.STAT_POS));
                case HIQ.REQUEST_READ -> read(((Long) intr.values[0]).intValue());
                case HIQ.REQUEST_WRITE -> write(((Long) intr.values[0]).intValue(), ((Long) intr.values[1]).intValue());
                case HIQ.REQUEST_IO_READ -> read((int) intr.values[0], (String) intr.values[1]);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    public int size() {
        return size;
    }

}
