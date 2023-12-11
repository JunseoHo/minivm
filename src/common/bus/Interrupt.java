package common.bus;

public class Interrupt {

    private final String receiver;
    private final int id;
    private final Object[] values;

    public Interrupt(String receiver, int id, Object... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

    public String receiver() { return receiver; }

    public int id() {
        return id;
    }

    public Object[] values() {
        return values;
    }

}
