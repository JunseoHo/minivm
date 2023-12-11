package os.memory_manager;

import common.MiniVMUtils;
import common.SyncQueue;
import common.bus.SWInterrupt;
import hardware.ram.RAM;
import hardware.ram.RamHistory;
import os.OSModule;

import java.util.*;

public class MemoryManager extends OSModule {
    // hardware
    private final RAM ram;
    // attributes
    private static final int PAGE_SIZE = 64;
    // components
    private final Page[] pageTable;
    private final SyncQueue<PageHistory> histories = new SyncQueue<>();

    public MemoryManager(RAM ram) {
        // set associations
        this.ram = ram;
        // create components
        pageTable = new Page[ram.size() / PAGE_SIZE]; // frame size
        for (int i = 0; i < pageTable.length; i++) pageTable[i] = new Page(ram.malloc(PAGE_SIZE), PAGE_SIZE);
        // register isr
        registerISR(SWInterrupt.REQUEST_READ_MEMORY, this::read);
        registerISR(SWInterrupt.REQUEST_WRITE_MEMORY, this::write);
        registerISR(SWInterrupt.REQUEST_ALLOCATE_MEMORY, this::allocate);
        registerISR(SWInterrupt.REQUEST_FREE_MEMORY, this::free);
    }

    public void read(SWInterrupt intr) {
        int pageIndex = (int) intr.values()[0];
        int displacement = (int) intr.values()[1];
        if (displacement < 0 || displacement > PAGE_SIZE - 1)
            send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.SEGMENTATION_FAULT));
        else send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.RESPONSE_READ_MEMORY,
                ram.read(pageTable[pageIndex].physicalBase + displacement)));
    }

    public int read(int pageIndex, int displacement) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1)
            return -1;
        else return ram.read(pageTable[pageIndex].physicalBase + displacement);
    }

    public void write(SWInterrupt intr) {
        int pageIndex = (int) intr.values()[0];
        int displacement = (int) intr.values()[1];
        Integer val = (Integer) intr.values()[2];
        if (displacement < 0 || displacement > PAGE_SIZE - 1)
            send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.SEGMENTATION_FAULT));
        else {
            ram.write(pageTable[pageIndex].physicalBase + displacement, val);
            send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.RESPONSE_WRITE_MEMORY));
        }
    }

    public void write(int pageIndex, int displacement, int val) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1)
            return;
        else {
            ram.write(pageTable[pageIndex].physicalBase + displacement, val);
        }
    }

    public void allocate(SWInterrupt intr) {
        int size = (int) intr.values()[0];
        int pageCount = size / PAGE_SIZE + (size % PAGE_SIZE > 0 ? 1 : 0);
        List<Integer> allocatedPageTable = new LinkedList<>();
        for (int pageIndex = 0; pageIndex < pageTable.length; pageIndex++) {
            if (!pageTable[pageIndex].inUsed) allocatedPageTable.add(pageIndex);
            if (allocatedPageTable.size() == pageCount) break;
        }
        if (allocatedPageTable.size() < pageCount) {
            send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.OUT_OF_MEMORY));
            return;
        }
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex].inUsed = true;
        addHistory(new PageHistory(MiniVMUtils.ALLOCATED, size, allocatedPageTable.size() * PAGE_SIZE));
        send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.RESPONSE_ALLOCATE_MEMORY, allocatedPageTable));
    }

    public void free(SWInterrupt intr) {
        List<Integer> allocatedPageTable = (List<Integer>) intr.values()[0];
        addHistory(new PageHistory(MiniVMUtils.FREE, allocatedPageTable.size() * PAGE_SIZE, allocatedPageTable.size() * PAGE_SIZE));
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex].inUsed = false;
        send(new SWInterrupt(SWInterrupt.PM, SWInterrupt.RESPONSE_FREE_MEMORY));
    }

    public SyncQueue<RamHistory> getRAMHistories() {
        return ram.getHistories();
    }

    public SyncQueue<PageHistory> getPageHistories() {
        return histories;
    }

    public Page[] getPageTable() {
        return pageTable;
    }

    private void addHistory(PageHistory history) {
        while (histories.size() > 10) histories.poll();
        histories.add(history);
    }

    public Page getPage(int index) {
        return pageTable[index];
    }
}
