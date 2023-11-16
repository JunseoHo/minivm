package os.memory_manager;

public class Page {

    public int frameIndex;
    public int size;

    public Page(int frameIndex, int size) {
        this.frameIndex = frameIndex;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Page [" + frameIndex + ", " + size + "]";
    }

}
