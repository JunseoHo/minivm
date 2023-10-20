package os;

import common.bus.Event;

public class SIQ extends Event {

    public Object[] values;

    public SIQ(String receiver, int id) {
        this(receiver, id, null);
    }

    public SIQ(String receiver, int id, Object... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

    // interrupt ids
    public static final int REQUEST_NEW_PROCESS = 0x00;
    public static final int REQUEST_PAGES = 0x32;
    public static final int RESPONSE_PAGES = 0x33;
    public static final int OUT_OF_MEMORY = 0x34;
    public static final int REQUEST_MEMORY_WRITE = 0x35;
    public static final int RESPONSE_MEMORY_WRITE = 0x36;
    public static final int REQUEST_FILE = 0x64;
    public static final int RESPONSE_FILE = 0x65;
    public static final int FILE_NOT_FOUND = 0x66;

    @Override
    public String toString() {
        return "SIQ [" + id + "] to " + receiver;
    }

}
