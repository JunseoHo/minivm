package os.memory_manager;

public class Page {

    public int base;
    public int size;

    public Page(int base, int size, boolean inUsed) {
        this.base = base;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Page [" + base + ", " + size + "]";
    }

}
