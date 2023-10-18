package hardware.interrupt;

import common.Event;

public class IOInterrupt extends Event {
    public int id;

    public IOInterrupt(String receiver, int id) {
        this.receiver = receiver;
        this.id = id;
    }

}
