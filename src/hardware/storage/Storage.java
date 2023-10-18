package hardware.storage;

import hardware.interrupt.IOInterrupt;
import hardware.io_device.IODevice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage extends IODevice {
    // attributes
    private static final String DISK_IMAGE_PATH = "/src/hardware/storage/DISK_IMAGE";
    private int size = 0;
    private List<Long> storage = new ArrayList<>();

    public Storage() {
        importDiskImage();
    }

    @Override
    public synchronized long read(int addr) {
        if (addr < 0 || addr > size - 1) {
            send(null);
            return 0;
        } else return storage.get(addr);
    }

    @Override
    public synchronized void write(int addr, long val) {
        if (addr < 0 || addr > size - 1) send(null);
        else storage.set(addr, val);
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
        IOInterrupt interrupt;
        if ((interrupt = receive()) != null) {
            switch (interrupt.id) {
                case 0x00 -> send(new IOInterrupt("CPU", 1));
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
