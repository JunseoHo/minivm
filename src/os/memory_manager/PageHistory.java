package os.memory_manager;

public class PageHistory {

    public String method;
    public int size;
    public int bytes;

    public PageHistory(String method, int size, int bytes) {
        this.method = method;
        this.size = size;
        this.bytes = bytes;
    }

}
