package hardware;

import common.bus.Event;

public class HIQ extends Event {

    // attributes
    public Object values[];

    public HIQ(String receiver, int id) {
        this(receiver, id, null);
    }

    public HIQ(String receiver, int id, Object... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

    @Override
    public String toString() {
        return "HIQ[" + id + "]";
    }

    // interrupt ids
    public static final int STAT_CHK = 0x00;
    public static final int STAT_POS = 0x01;
    public static final int STAT_NEG = 0x02;
    public static final int REQUEST_READ = 0x03;
    public static final int RESPONSE_READ = 0x04;
    public static final int REQUEST_WRITE = 0x05;
    public static final int RESPONSE_WRITE = 0x06;
    public static final int HALT = 0x07;
    public static final int RESPONSE_TERMINATE = 0x08;
    public static final int TIME_SLICE_EXPIRED = 0x09;
    public static final int RESPONSE_SWITCH_CONTEXT = 0x10;
    public static final int REQUEST_IO_WRITE = 0x11;
    public static final int RESPONSE_IO_WRITE = 0x12;
    public static final int REQUEST_IO_READ = 0x13;
    public static final int RESPONSE_IO_READ = 0x14;
    public static final int COMPLETE_IO = 0x15;
    public static final int SEGFAULT = 0x40;
}
