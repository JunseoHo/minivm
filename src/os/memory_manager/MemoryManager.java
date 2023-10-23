package os.memory_manager;

import common.CircularQueue;
import common.InterruptServiceRoutine;
import hardware.Memory;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SIRQ;
import os.SWName;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager extends OSModule {

    private static final int PAGE_SIZE = 64;
    private Memory memory;
    private CircularQueue<Page> pageQueue = new CircularQueue<>(100);

    public MemoryManager() {
        registerInterruptServiceRoutine(SIRQ.REQUEST_PAGES, (intr) -> getPages(intr));
        registerInterruptServiceRoutine(SIRQ.REQUEST_MEMORY_WRITE, (intr) -> write(intr));
        registerInterruptServiceRoutine(SIRQ.REQUEST_FREE_PAGE, (intr) -> freePages(intr));
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }

    public void associate(IODevice ioDevice) {
        memory = (Memory) ioDevice;
        int pageNum = memory.bufferSize() / PAGE_SIZE;
        for (int num = 0; num < pageNum; num++) pageQueue.enqueue(new Page(num * PAGE_SIZE, PAGE_SIZE));
    }

    @InterruptServiceRoutine
    public synchronized void getPages(SIRQ intr) {
        int pageNum = (int) intr.values[0];
        if (pageNum > pageQueue.size()) send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.OUT_OF_MEMORY));
        else {
            List<Page> pages = new ArrayList<>();
            for (int index = 0; index < pageNum; index++) pages.add(pageQueue.dequeue());
            send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.RESPONSE_PAGES, pages));
        }
    }

    @InterruptServiceRoutine
    public synchronized void write(SIRQ intr) {
        int base = (int) intr.values[0];
        List<Long> records = (List<Long>) intr.values[1];
        for (Long record : records) memory.write(base++, record);
        send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.RESPONSE_MEMORY_WRITE));
    }

    @InterruptServiceRoutine
    public synchronized void freePages(SIRQ intr) {
        List<Page> pages = (List<Page>) intr.values[0];
        for (Page page : pages) pageQueue.enqueue(page);
        send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.RESPONSE_FREE_PAGE));
    }

    @Override
    public String toString() {
        return "[Memory manager]\n"
                + "Page size       : " + PAGE_SIZE + "\n"
                + "Available pages : " + pageQueue.size();
    }
}
