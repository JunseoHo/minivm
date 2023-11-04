package hardware.hdd;

import hardware.IODevice;

import java.util.ArrayList;
import java.util.List;

public class HDD implements IODevice {

    private static final int FLATTER_SIZE = 16384;
    private static final int FLATTER_COUNT = 4;
    private final List<Flatter> flatters = new ArrayList<>();

    public HDD() {
        for (int i = 0; i < FLATTER_COUNT; i++)
            flatters.add(new Flatter(FLATTER_SIZE));
    }

    @Override
    public Byte read(int addr) {
        if (addr < 0 || addr > size() - 1) return null;
        System.out.println(addr);
        return flatters.get(addr / FLATTER_SIZE).read(addr % FLATTER_SIZE);
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
        if (addr < 0 || addr > size() - 1) return false;
        return flatters.get(addr / FLATTER_SIZE).write(addr % FLATTER_SIZE, val);
    }

    @Override
    public boolean write(int addr, byte[] val) {
        for (int i = 0; i < val.length; i++)
            if (!write(addr + i, val[i])) return false;
        return true;
    }

    @Override
    public int size() {
        return FLATTER_SIZE * FLATTER_COUNT;
    }
}
