package hardware.mmu;

import os.memory_manager.MemoryManager;
import os.memory_manager.Page;
import os.memory_manager.RAMDriver;

import java.util.List;

public class MMU {

    private List<Page> pageTable = null;
    private MemoryManager memoryManager;

    public void setPageTable(List<Page> pageTable) {
        this.pageTable = pageTable;
    }

    public Byte read(int logicalAddr) {
        int p = logicalAddr >> 6;
        int d = logicalAddr & 63;
        return memoryManager.read(pageTable.get(p).frameIndex, d);
    }

    public boolean write(int logicalAddr, Byte val) {
        int p = logicalAddr >> 6;
        int d = logicalAddr & 63;
        return memoryManager.write(pageTable.get(p).frameIndex, d, val);
    }

    public void associate(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }
}
