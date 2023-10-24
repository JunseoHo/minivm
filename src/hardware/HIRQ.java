package hardware;

import common.bus.Event;

public class HIRQ extends Event {

    public static final int REQUEST_READ = 0x00;
    public static final int RESPONSE_READ = 0x01;
    public static final int REQUEST_WRITE = 0x02;
    public static final int RESPONSE_WRITE = 0x03;
    public static final int RESPONSE_TERMINATE = 0x04;
    public static final int COMPLETE_IO = 0x05;
    public static final int HALT = 0x06;
    public static final int TIME_SLICE_EXPIRED = 0x07;
    public static final int SEGMENTATION_FAULT = 0x40;

    public HIRQ(String receiver, int id) {
        this(receiver, id, null);
    }

    public HIRQ(String receiver, int id, Object... values) {
        super(receiver, id, values);
    }

    @Override
    public String toString() {
        return "HIRQ[" + id() + "]";
    }
}
