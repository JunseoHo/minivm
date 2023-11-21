package hardware.intr;

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
        String intrName;
        switch (id()) {
            case REQUEST_READ -> intrName = "REQUEST_READ";
            case RESPONSE_READ -> intrName = "RESPONSE_READ";
            case REQUEST_WRITE -> intrName = "REQUEST_WRITE";
            case RESPONSE_WRITE -> intrName = "RESPONSE_WRITE";
            case RESPONSE_TERMINATE -> intrName = "RESPONSE_TERMINATE";
            case COMPLETE_IO -> intrName = "COMPLETE_IO";
            case HALT -> intrName = "HALT";
            case TIME_SLICE_EXPIRED -> intrName = "TIME_SLICE_EXPIRED";
            case SEGMENTATION_FAULT -> intrName = "SEGMENTATION_FAULT";
            default -> intrName = "Unknown";
        }
        return "HIRQ[" + intrName + "]";
    }
}
