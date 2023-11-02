package interrupt;

public class IRQ {

    public Intr id;
    public String receiver;
    public Object[] params;

    public IRQ(Intr id, String receiver, Object... params) {
        this.id = id;
        this.receiver = receiver;
        this.params = params;
    }

    public IRQ(Intr id, String receiver) {
        this.id = id;
        this.receiver = receiver;
        this.params = null;
    }

    public static final int HALT = 0x00;
    public static final int TIME_SLICE_EXPIRED = 0x01;

}
