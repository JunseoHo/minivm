package hardware.ram;

import hardware.IODevice;

public class RAM implements IODevice {

    private static final int size = 131072;
    private final Byte[] memories;

    public RAM() {
        memories = new Byte[size];
    }

    @Override
    public Byte read(int addr) {
        if (addr < 0 || addr > size - 1) return null;
        return memories[addr];
    }

    @Override
    public Byte[] read(int addr, int size) {
        Byte[] values = new Byte[size];
        for (int i = 0; i < size; i++)
            if ((values[i] = read(addr + i)) == null) return null;
        return values;
    }

    @Override
    public boolean write(int addr, byte val) {
        if (addr < 0 || addr > size - 1) return false;
        memories[addr] = val;
        return true;
    }

    @Override
    public boolean write(int addr, byte[] val) {
        for (int i = 0; i < val.length; i++)
            if (!write(addr + i, val[i])) return false;
        return true;
    }

    @Override
    public int size() {
        return size;
    }
}
