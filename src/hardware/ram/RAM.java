package hardware.ram;

import hardware.IODevice;

public class RAM {

    private static final int size = 32768;
    private final Integer[] memories;

    public RAM() {
        memories = new Integer[size];
    }

    public Integer read(int addr) {
        if (addr < 0 || addr > size - 1) return null;
        return memories[addr];
    }

    public Integer[] read(int addr, int size) {
        Integer[] values = new Integer[size];
        for (int i = 0; i < size; i++)
            if ((values[i] = read(addr + i)) == null) return null;
        return values;
    }

    public boolean write(int addr, Integer val) {
        if (addr < 0 || addr > size - 1) return false;
        memories[addr] = val;
        return true;
    }

    public boolean write(int addr, Integer[] val) {
        for (int i = 0; i < val.length; i++)
            if (!write(addr + i, val[i])) return false;
        return true;
    }

    public int size() {
        return size;
    }
}
