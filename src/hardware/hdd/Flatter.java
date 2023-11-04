package hardware.hdd;

import java.util.ArrayList;
import java.util.List;

public class Flatter {

    private static final int SECTOR_SIZE = 4;
    private final int size;
    private final List<Sector> sectors = new ArrayList<>();

    public Flatter(int size) {
        this.size = size;
        for (int i = 0; i < size / SECTOR_SIZE; i++)
            sectors.add(new Sector(SECTOR_SIZE));
    }

    public Byte read(int addr) {
        if (addr < 0 || addr > size - 1) return null;
        return sectors.get(addr / SECTOR_SIZE).read(addr % SECTOR_SIZE);
    }

    public boolean write(int addr, Byte val) {
        if (addr < 0 || addr > size - 1) return false;
        return sectors.get(addr / SECTOR_SIZE).write(addr % SECTOR_SIZE, val);
    }

}
