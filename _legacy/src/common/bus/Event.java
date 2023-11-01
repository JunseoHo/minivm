package interrupt.bus;

public class Event {

    private final String receiver;
    private final int id;
    private final Object[] values;

    public Event(String receiver, int id, Object... values) {
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
