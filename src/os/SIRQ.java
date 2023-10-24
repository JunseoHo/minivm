package os;

import common.bus.Event;

public class SIRQ extends Event {

    public static final int REQUEST_LOAD_PROCESS = 0x00;
    public static final int REQUEST_SWITCH_CONTEXT = 0x01;
    public static final int REQUEST_TERMINATE_PROCESS = 0x02;
    public static final int REQUEST_PAGES = 0x32;
    public static final int RESPONSE_PAGES = 0x33;
    public static final int OUT_OF_MEMORY = 0x34;
    public static final int REQUEST_MEMORY_WRITE = 0x35;
    public static final int RESPONSE_MEMORY_WRITE = 0x36;
    public static final int REQUEST_FREE_PAGE = 0x37;
    public static final int RESPONSE_FREE_PAGE = 0x38;
    public static final int REQUEST_FILE = 0x64;
    public static final int RESPONSE_FILE = 0x65;
    public static final int FILE_NOT_FOUND = 0x66;
    public static final int REQUEST_IO_WRITE = 0x96;
    public static final int RESPONSE_IO_WRITE = 0x97;
    public static final int COMPLETE_IO = 0x98;

    public SIRQ(String receiver, int id) {
        this(receiver, id, null);
    }

    public SIRQ(String receiver, int id, Object... values) {
        super(receiver, id, values);
    }

    @Override
    public String toString() {
        return "SIRQ[" + id() + "]";
    }

}
