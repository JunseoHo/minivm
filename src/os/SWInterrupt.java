package os;

import common.bus.Event;

public class SWInterrupt extends Event {

    public int id;
    public Object[] values;

    public SWInterrupt(String receiver, int id) {
        this(receiver, id, null);
    }

    public SWInterrupt(String receiver, int id, Object... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

}
