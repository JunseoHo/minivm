package hardware;

import hardware.io_device.IODevice;

public class Memory extends IODevice {

    // attributes
    private static final int MIN_SIZE = 128;
    private static final int MAX_SIZE = 8192;
    private static final int DEFAULT_SIZE = 1024;
    private int size = 0;
    private long[] memory;

    public Memory() {
        this(DEFAULT_SIZE);
    }

    public Memory(int size) {
        if (size < MIN_SIZE || size > MAX_SIZE) {
            System.err.println("memory size is too large.");
            size = DEFAULT_SIZE;
        }
        memory = new long[this.size = size];
    }

    @Override
    public synchronized void read(int addr) {
        if (addr < 0 || addr > size - 1) send(new IOInterrupt("CPU", 0x40));
        else send(new IOInterrupt("CPU", 0x04, memory[addr]));
    }

    @Override
    public synchronized void write(int addr, long val) {
        if (addr < 0 || addr > size - 1) send(null);
        else memory[addr] = val;
    }

    @Override
    public void handleInterrupt() {
        IOInterrupt interrupt;
        if ((interrupt = receive()) != null) {
            switch (interrupt.id) {
                case 0x00 -> send(new IOInterrupt("CPU", 1));
                case 0x03 -> read((int) interrupt.values[0]);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

}
