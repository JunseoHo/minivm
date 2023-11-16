package os.memory_manager;

import hardware.mmu.MMU;
import hardware.ram.RAM;

import java.util.*;

public class MemoryManager {
    // attributes
    private static final int PAGE_SIZE = 64;
    // components
    private RAMDriver ramDriver;
    private final Queue<Page> pageTable = new LinkedList<>();

    public MemoryManager(MMU mmu, RAM ram) {
        mmu.associate(this);
        this.ramDriver = new RAMDriver(ram);
        int frameIndex;
        while ((frameIndex = ramDriver.allocate(PAGE_SIZE)) != -1)
            pageTable.add(new Page(frameIndex, PAGE_SIZE));
    }

    public Byte read(int frameIndex, int offset) {
        return ramDriver.read(frameIndex, offset);
    }

    public boolean write(int frameIndex, int offset, Byte val) {
        return ramDriver.write(frameIndex, offset, val);
    }

    public List<Page> allocate(int size) {
        int pageCount = size / PAGE_SIZE + 1;
        if (pageTable.size() < pageCount) return null; // out of page
        List<Page> allocatedPages = new LinkedList<>();
        for (int i = 0; i < pageCount; i++) allocatedPages.add(pageTable.poll());
        return allocatedPages;
    }

    public void free(List<Page> pages) {
        pageTable.addAll(pages);
    }

}
