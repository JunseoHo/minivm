package os.memory_manager;

import common.CircularQueue;
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

    @Override
    public void run() {
        while (true) handleInterrupt();
    }

    public void associate(IODevice ioDevice) {
        memory = (Memory) ioDevice;
        int pageNum = memory.size() / PAGE_SIZE;
        for (int num = 0; num < pageNum; num++) pageQueue.enqueue(new Page(num * PAGE_SIZE, PAGE_SIZE));
    }

    public Page getPage() {
        if (!pageQueue.isEmpty()) return pageQueue.dequeue();
        return null;
    }

    public synchronized void getPages(int num) {
        if (num > pageQueue.size()) send(new SIQ(SWName.PROCESS_MANAGER, SIQ.OUT_OF_MEMORY));
        else {
            List<Page> pages = new ArrayList<>();
            for (int index = 0; index < num; index++) pages.add(pageQueue.dequeue());
            send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_PAGES, pages));
        }
    }

    public synchronized void write(int base, List<Long> records) {
        for (Long record : records) memory.write(base++, record);
        send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_MEMORY_WRITE));
    }

    @Override
    public void handleInterrupt() {
        for (SIQ intr : receiveAll()) queue.enqueue(intr);
        while (!queue.isEmpty()) {
            SIQ intr = queue.dequeue();
            switch (intr.id) {
                case SIQ.REQUEST_PAGES -> getPages((Integer) intr.values[0]);
                case SIQ.REQUEST_MEMORY_WRITE -> write((Integer) intr.values[0], (List<Long>) intr.values[1]);
            }
        }
    }
}
