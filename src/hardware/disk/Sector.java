package hardware.disk;


import java.util.Arrays;

public class Sector {

    private final int SIZE;
    private final Byte[] MEMORIES;

    public Sector(int size) {
        Arrays.fill(MEMORIES = new Byte[this.SIZE = size], (byte) 0);
    }

    public Byte read(int addr) {
        if (addr < 0 || addr > SIZE - 1) return null;
        return MEMORIES[addr];
    }

    public boolean write(int addr, Byte val) {
        if (addr < 0 || addr > SIZE - 1) return false;
        MEMORIES[addr] = val;
        return true;
    }
}
