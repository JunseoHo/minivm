package hardware.storage;

import hardware.HIRQ;
import hardware.HWName;
import hardware.io_device.IODevice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage extends IODevice {

    private static final String DISK_IMAGE_PATH = "/src/hardware/storage/DISK_IMAGE";
    private List<Long> storage = new ArrayList<>();

    public Storage() {
        // import disk to storage
        importDiskImage();
        // register interrupt service routines
        registerInterruptServiceRoutine(HIRQ.REQUEST_READ, this::read);
        registerInterruptServiceRoutine(HIRQ.REQUEST_WRITE, this::write);
    }

    @Override
    public synchronized void read(HIRQ intr) {
        int addr = (int) intr.values()[0];
        String receiver = (String) intr.values()[1];
        if (addr < 0 || addr > storage.size() - 1) send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        else send(new HIRQ(receiver, HIRQ.RESPONSE_READ, storage.get(addr)));
    }

    @Override
    public synchronized void write(HIRQ intr) {
        int addr = (int) intr.values()[0];
        long val = (long) intr.values()[1];
        String receiver = (String) intr.values()[2];
        if (addr < 0 || addr > storage.size() - 1) send(new HIRQ(receiver, HIRQ.SEGMENTATION_FAULT));
        else send(new HIRQ(receiver, HIRQ.RESPONSE_WRITE, storage.set(addr, val)));
    }

    @Override
    public int bufferSize() {
        return storage.size();
    }

    public synchronized long readRecord(int addr) {
        return storage.get(addr);
    }

    private boolean importDiskImage() {
        try (Scanner sc = new Scanner(new File(System.getProperty("user.dir") + DISK_IMAGE_PATH))) {
            while (sc.hasNextLine()) storage.add(Long.parseLong(sc.nextLine()));
            return true;
        } catch (NumberFormatException | FileNotFoundException e) {
            System.err.println("failed to import disk image.");
            return false;
        }
    }

}
