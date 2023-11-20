package os.memory_manager;

import hardware.ram.RAM;

import java.util.*;

public class MemoryManager {
    // hardware
    private RAM ram;
    // attributes
    private static final int PAGE_SIZE = 64;
    // components
    private final Boolean[] pageTable;

    public MemoryManager(RAM ram) {
        // set associations
        this.ram = ram;
        // create components
        pageTable = new Boolean[ram.size() / PAGE_SIZE];
        Arrays.fill(pageTable, false);
    }

    public Byte read(int pageIndex, int displacement) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1) return null;
        return ram.read(pageIndex * PAGE_SIZE + displacement);
    }

    public boolean write(int pageIndex, int displacement, Byte val) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1) return false;
        return ram.write(pageIndex * PAGE_SIZE + displacement, val);
    }


    public List<Integer> allocate(int size) {
        int pageCount = size / PAGE_SIZE + (size % PAGE_SIZE > 0 ? 1 : 0);
        List<Integer> allocatedPageTable = new LinkedList<>();
        for (int pageIndex = 0; pageIndex < pageTable.length; pageIndex++) {
            if (!pageTable[pageIndex]) allocatedPageTable.add(pageIndex);
            if (allocatedPageTable.size() == pageCount) break;
        }
        if (allocatedPageTable.size() < pageCount) return null;
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex] = true;
        return allocatedPageTable;
    }

    public void free(List<Integer> allocatedPageTable) {
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex] = false;
    }

}
