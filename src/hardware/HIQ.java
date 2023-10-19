package hardware;

import common.bus.Event;

public class HIQ extends Event {

    // attributes
    public long values[];

    public HIQ(String receiver, int id) {
        this(receiver, id, null);
    }

    public HIQ(String receiver, int id, long... values) {
        this.receiver = receiver;
        this.id = id;
        this.values = values;
    }

    // interrupt ids
    public static final int STAT_CHK = 0x00;
    public static final int STAT_POS = 0x01;
    public static final int STAT_NEG = 0x02;
    public static final int CPU_READ = 0x03;
    public static final int READ_RESPONSE = 0x04;
    public static final int CPU_WRITE = 0x05;
    public static final int WRITE_RESPONSE = 0x06;
    public static final int HALT = 0x07;
    public static final int TIME_SLICE_EXPIRED = 0x08;
    public static final int SEGFAULT = 0x40;
}
