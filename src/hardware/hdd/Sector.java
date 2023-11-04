package hardware.hdd;


import java.lang.reflect.Array;
import java.util.Arrays;

public class Sector {

    private final int size;
    private final Byte[] memories;

    public Sector(int size) {
        this.size = size;
        memories = new Byte[size];
        Arrays.fill(memories, (byte) 0);
    }

    public Byte read(int addr) {
        if (addr < 0 || addr > size - 1) return null;
        return memories[addr];
    }

    public boolean write(int addr, Byte val) {
        if (addr < 0 || addr > size - 1) return false;
        memories[addr] = val;
        return true;
    }
}
