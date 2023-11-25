package os.memory_manager;

import hardware.ram.RAM;

import java.util.*;

public class MemoryManager {
    // hardware
    private final RAM ram;
    // attributes
    private static final int PAGE_SIZE = 64;
    // components
    private final Boolean[] pageTable;
    private final Queue<String> histories = new LinkedList<>();

    public MemoryManager(RAM ram) {
        // set associations
        this.ram = ram;
        // create components
        pageTable = new Boolean[ram.size() / PAGE_SIZE];
        Arrays.fill(pageTable, false);
    }

    public Integer read(int pageIndex, int displacement) {
        if (displacement < 0 || displacement > PAGE_SIZE - 1) return null;
        return ram.read(pageIndex * PAGE_SIZE + displacement);
    }

    public boolean write(int pageIndex, int displacement, Integer val) {
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
        addHistory(String.format("%-12s%-20s%s", "ALLOCATE, ", "Size : " + size + ", ", "Result : " + allocatedPageTable.size() * PAGE_SIZE + " bytes allocated"));
        return allocatedPageTable;
    }

    public void free(List<Integer> allocatedPageTable) {
        addHistory(String.format("%-12s%-20s%s", "FREE, ", "Size : " + allocatedPageTable.size() * PAGE_SIZE, "Result : " + allocatedPageTable.size() * PAGE_SIZE + " bytes freed"));
        for (int pageIndex : allocatedPageTable) pageTable[pageIndex] = false;
    }

    public Queue<String> getRAMHistories() {
        return ram.getHistories();
    }

    public Queue<String> getHistories() {
        return histories;
    }

    public Boolean[] getPageTable() {
        return pageTable;
    }

    private void addHistory(String history) {
        while (histories.size() > 10) histories.poll();
        histories.add(history);
    }

}
