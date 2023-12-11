package os.memory_manager;

public class Page {

    public int physicalBase;
    public int size;
    public boolean inUsed;

    public Page(int physicalBase, int size) {
        this.physicalBase = physicalBase;
        this.size = size;
        this.inUsed = false;
    }

}
