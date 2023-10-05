package os.memory_manager;

import hardware.memory.Memory;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MemoryManager {

    // Associations
    private Memory memory = null;
    // attributes
    private static final int PAGE_SIZE = 32;
    private Queue<Page> pages = null;


    public MemoryManager(Memory memory) {
        this.memory = memory;
        pages = new LinkedList<>();
        for (int page = 0; page < memory.size() / PAGE_SIZE; page++)
            pages.add(new Page(page * PAGE_SIZE, page * PAGE_SIZE + PAGE_SIZE));
    }

    public Page getPage() {
        return pages.isEmpty() ? null : pages.poll();
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public void loadProgram(Page codeSegment, List<Long> program) {
        for (int idx = 0; idx < program.size(); idx++)
            memory.write(codeSegment.base + idx, program.get(idx));
    }
}
