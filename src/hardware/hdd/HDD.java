package hardware.hdd;

import hardware.IODevice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formattable;
import java.util.List;
import java.util.Scanner;

public class HDD implements IODevice {

    private static final String DISK_IMAGE_FILE_NAME = "/src/hardware/hdd/DISK_IMAGE";
    private static final int FLATTER_SIZE = 131072; // 128KB
    private static final int FLATTER_COUNT = 4;
    private final List<Flatter> flatters = new ArrayList<>();

    public HDD() {
        for (int i = 0; i < FLATTER_COUNT; i++) flatters.add(new Flatter(FLATTER_SIZE));
        importDiskImage();
    }

    private void importDiskImage() {
        try {
            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + DISK_IMAGE_FILE_NAME));
            for (int addr = 0; addr < FLATTER_SIZE * FLATTER_COUNT; addr++) {
                while (scanner.hasNextLine()) {
                    String[] sectorValues = scanner.nextLine().split(" ");
                    for (String sectorValue : sectorValues) write(addr++, Byte.parseByte(sectorValue, 16));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Disk image file not found.");
        }
    }

    @Override
    public Byte read(int addr) {
        if (addr < 0 || addr > size() - 1) return null;
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
