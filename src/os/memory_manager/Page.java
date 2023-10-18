package os.memory_manager;

public class Page {

    public int base;
    public int limit;

    public Page(int base, int limit) {
        this.base = base;
        this.limit = limit;
    }

}
