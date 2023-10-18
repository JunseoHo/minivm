package hardware;

import common.Event;

public class IOInterrupt extends Event {

    public int id;
    public long values[];

    public IOInterrupt(String receiver, int id) {
        this(receiver, id, 0);
    }

    public IOInterrupt(String receiver, int id, long... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

}
