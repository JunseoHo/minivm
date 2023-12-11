package os.memory_manager;

import common.MiniVMUtils;
import common.SyncQueue;
import hardware.ram.RAM;
import hardware.ram.RamHistory;

import java.util.*;

public class MemoryManager {
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
    }

    public Integer read(int pageIndex, int displacement) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1) return null;
        return ram.read(pageTable[pageIndex].physicalBase + displacement);
    }

    public boolean write(int pageIndex, int displacement, Integer val) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1) return false;
        return ram.write(pageTable[pageIndex].physicalBase + displacement, val);
    }

    public List<Integer> allocate(int size) {
        int pageCount = size / PAGE_SIZE + (size % PAGE_SIZE > 0 ? 1 : 0);
        List<Integer> allocatedPageTable = new LinkedList<>();
        for (int pageIndex = 0; pageIndex < pageTable.length; pageIndex++) {
            if (!pageTable[pageIndex].inUsed) allocatedPageTable.add(pageIndex);
            if (allocatedPageTable.size() == pageCount) break;
        }
        if (allocatedPageTable.size() < pageCount) return null;
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex].inUsed = true;
        addHistory(new PageHistory(MiniVMUtils.ALLOCATED, size, allocatedPageTable.size() * PAGE_SIZE));
        return allocatedPageTable;
    }

    public void free(List<Integer> allocatedPageTable) {
        addHistory(new PageHistory(MiniVMUtils.FREE, allocatedPageTable.size() * PAGE_SIZE, allocatedPageTable.size() * PAGE_SIZE));
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex].inUsed = false;
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

    public Page getPage(int index){
        return pageTable[index];
    }


}
