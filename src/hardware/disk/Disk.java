package hardware.disk;

import hardware.IODevice;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Disk implements IODevice {
    // attributes
    private final String DISK_IMAGE_FILE_NAME = "/src/hardware/disk/DISK_IMAGE";
    private final int FLATTER_SIZE = 131072; // 128KB
    private final int FLATTER_COUNT = 4;
    // components
    private final List<Flatter> flatters = new ArrayList<>();

    public Disk() {
        for (int i = 0; i < FLATTER_COUNT; i++) flatters.add(new Flatter(FLATTER_SIZE));
        importDiskImage();
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

    public String getImage(int begin, int end) {
        String img = "";
        if (begin >= end) return img;
        if (begin % 8 != 0) begin -= begin % 8;
        if (end % 8 != 0) end += 8 - (end % 8);
        for (; begin <= end; begin += 8) {
            byte[] values = new byte[8];
            for (int i = 0; i < 8; i++) values[i] = read(begin + i);
            img += String.format("%-8d  %4d%4d%4d%4d%4d%4d%4d%4d   %c%c%c%c%c%c%c%c\n", begin,
                    values[0], values[1], values[2], values[3],
                    values[4], values[5], values[6], values[7],
                    asPrintable(values[0]), asPrintable(values[1]),
                    asPrintable(values[2]), asPrintable(values[3]),
                    asPrintable(values[4]), asPrintable(values[5]),
                    asPrintable(values[6]), asPrintable(values[7]));
        }
        return img;
    }

    private void importDiskImage() {
        try {
            Scanner scanner = new Scanner(new File(System.getProperty("user.dir") + DISK_IMAGE_FILE_NAME));
            for (int addr = 0; addr < FLATTER_SIZE * FLATTER_COUNT; addr++) {
                while (scanner.hasNextLine())
                    for (String sectorValue : scanner.nextLine().split(" "))
                        write(addr++, Byte.parseByte(sectorValue, 16));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Disk image file not found.");
        }
    }

    private char asPrintable(byte c) {
        return c > 31 && c < 127 ? (char) c : '.';
    }

}
