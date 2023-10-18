package hardware;

import common.Event;

public class HWInterrupt extends Event {

    public int id;
    public long values[];

    public HWInterrupt(String receiver, int id) {
        this(receiver, id, 0);
    }

    public HWInterrupt(String receiver, int id, long... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

}
