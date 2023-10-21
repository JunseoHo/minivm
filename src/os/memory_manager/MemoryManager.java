package os.memory_manager;

import common.CircularQueue;
import common.InterruptServiceRoutine;
import hardware.Memory;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SIQ;
import os.SWName;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager extends OSModule {

    private static final int PAGE_SIZE = 64;
    private Memory memory;
    private CircularQueue<Page> pageQueue = new CircularQueue<>(100);

    public MemoryManager() {
        registerInterruptServiceRoutine(SIQ.REQUEST_PAGES, (intr) -> getPages(intr));
        registerInterruptServiceRoutine(SIQ.REQUEST_MEMORY_WRITE, (intr) -> write(intr));
        registerInterruptServiceRoutine(SIQ.REQUEST_FREE_PAGE, (intr) -> freePages(intr));
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }

    public void associate(IODevice ioDevice) {
        memory = (Memory) ioDevice;
        int pageNum = memory.size() / PAGE_SIZE;
        for (int num = 0; num < pageNum; num++) pageQueue.enqueue(new Page(num * PAGE_SIZE, PAGE_SIZE));
    }

    @InterruptServiceRoutine
    public synchronized void getPages(SIQ intr) {
        int pageNum = (int) intr.values[0];
        if (pageNum > pageQueue.size()) send(new SIQ(SWName.PROCESS_MANAGER, SIQ.OUT_OF_MEMORY));
        else {
            List<Page> pages = new ArrayList<>();
            for (int index = 0; index < pageNum; index++) pages.add(pageQueue.dequeue());
            send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_PAGES, pages));
        }
    }

    @InterruptServiceRoutine
    public synchronized void write(SIQ intr) {
        int base = (int) intr.values[0];
        List<Long> records = (List<Long>) intr.values[1];
        for (Long record : records) memory.write(base++, record);
        send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_MEMORY_WRITE));
    }

    @InterruptServiceRoutine
    public synchronized void freePages(SIQ intr) {
        List<Page> pages = (List<Page>) intr.values[0];
        for (Page page : pages) pageQueue.enqueue(page);
        send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_FREE_PAGE));
    }

    @Override
    public String toString() {
        return "[Memory manager]\n"
                + "Page size       : " + PAGE_SIZE + "\n"
                + "Available pages : " + pageQueue.size();
    }
}
