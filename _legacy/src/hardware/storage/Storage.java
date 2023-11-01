package hardware.storage;

import interrupt.logger.MiniVMLogger;
import hardware.HIRQ;
import hardware.io_device.IODevice;

import java.io.*;
import java.util.Scanner;

public class Storage extends IODevice {

    private static final String DISK_IMAGE_PATH = "/src/hardware/storage/DISK_IMAGE";

    public Storage() {
        super();
        init();
    }

    public Storage(int bufferSize) {
        super(bufferSize);
        init();
    }

    @Override
    protected void init() {
        importDiskImage();
        registerISR(HIRQ.REQUEST_READ, this::read);
        registerISR(HIRQ.REQUEST_WRITE, this::write);
    }

    private void importDiskImage() {
        try (Scanner sc = new Scanner(new File(System.getProperty("user.dir") + DISK_IMAGE_PATH))) {
            int addr = 0;
            while (addr < bufferSize() && sc.hasNextLine()) writeBuffer(addr++, Long.parseLong(sc.nextLine()));
            if (addr == bufferSize() && sc.hasNextLine()) MiniVMLogger.warn("Storage", "There is not enough buffer size.");
        } catch (NumberFormatException | FileNotFoundException e) {
            MiniVMLogger.error("Storage", "Failed to import disk image");
        }
    }

}
