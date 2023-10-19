package hardware.storage;

import hardware.HIQ;
import hardware.HWName;
import hardware.io_device.IODevice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage extends IODevice {

    private static final String DISK_IMAGE_PATH = "/src/hardware/storage/DISK_IMAGE";
    private int size = 0;
    private List<Long> storage = new ArrayList<>();

    public Storage() {
        importDiskImage();
    }

    @Override
    public synchronized void read(int addr) {
        if (addr < 0 || addr > size - 1) send(new HIQ(HWName.CPU, HIQ.SEGFAULT));
        else send(new HIQ(HWName.CPU, HIQ.READ_RESPONSE, storage.get(addr)));
    }

    @Override
    public synchronized void write(int addr, long val) {
        if (addr < 0 || addr > size - 1) send(new HIQ(HWName.CPU, HIQ.SEGFAULT));
        else send(new HIQ(HWName.CPU, HIQ.WRITE_RESPONSE, storage.set(addr, val)));
    }

    private boolean importDiskImage() {
        try (Scanner sc = new Scanner(new File(System.getProperty("user.dir") + DISK_IMAGE_PATH))) {
            while (sc.hasNextLine()) storage.add(Long.parseLong(sc.nextLine()));
            size = storage.size();
            return true;
        } catch (NumberFormatException | FileNotFoundException e) {
            System.err.println("failed to import disk image.");
            return false;
        }
    }

    @Override
    public void handleInterrupt() {
        HIQ intr;
        if ((intr = receive()) != null) {
            switch (intr.id) {
                case HIQ.STAT_CHK -> send(new HIQ(HWName.CPU, HIQ.STAT_POS));
                case HIQ.CPU_READ -> read((int) intr.values[0]);
                case HIQ.CPU_WRITE -> write((int) intr.values[0], intr.values[1]);
            }
        }
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }
}
