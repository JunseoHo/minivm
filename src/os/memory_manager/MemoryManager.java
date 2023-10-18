package os.memory_manager;

import common.CircularQueue;
import hardware.Memory;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SWInterrupt;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager extends OSModule {

    private static final int PAGE_SIZE = 64;
    private Memory memory;
    private CircularQueue<Page> pages = new CircularQueue<>(100);

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    public void associate(IODevice ioDevice) {
        memory = (Memory) ioDevice;
        int pageNum = memory.size() / PAGE_SIZE;
        for (int num = 0; num < pageNum; num++) pages.enqueue(new Page(num * PAGE_SIZE, PAGE_SIZE));
    }

    public Page getPage() {
        if (!pages.isEmpty()) return pages.dequeue();
        return null;
    }

    public synchronized void allocPages(SWInterrupt interrupt) {
        int pageNum = (int) interrupt.values[0];
        if (pageNum > pages.size()) send(new SWInterrupt("ProcessManager", 0x34));
        else {
            List<Page> requestedPages = new ArrayList<>();
            for (int index = 0; index < pageNum; index++) requestedPages.add(pages.dequeue());
            send(new SWInterrupt("ProcessManager", 0x33, requestedPages));
        }
    }

    public synchronized void writeMemory(SWInterrupt interrupt) {
        int base = (int) interrupt.values[0];
        List<Long> values = (List<Long>) interrupt.values[1];
        for (Long value : values) memory.write(base++, value);
        send(new SWInterrupt("ProcessManager", 0x36));
    }

    @Override
    public void handleInterrupt() {
        for (SWInterrupt interrupt : receiveAll()) interruptQueue.enqueue(interrupt);
        while (!interruptQueue.isEmpty()) {
            interrupt = interruptQueue.dequeue();
            switch (interrupt.id) {
                case 0x32 -> allocPages(interrupt);
                case 0x35 -> writeMemory(interrupt);
            }
        }
    }
}
