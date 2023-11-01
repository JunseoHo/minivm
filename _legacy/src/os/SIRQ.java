package os;

import interrupt.bus.Event;

public class SIRQ extends Event {

    public static final int REQUEST_LOAD_PROCESS = 0x00;
    public static final int REQUEST_SWITCH_CONTEXT = 0x01;
    public static final int REQUEST_TERMINATE_PROCESS = 0x02;
    public static final int REQUEST_KILL_PROCESS = 0x03;
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
    public static final int REQUEST_CHANGE_DIRECTORY = 0x67;
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
        String intrName;
        switch (id()) {
            case REQUEST_LOAD_PROCESS -> intrName = "REQUEST_LOAD_PROCESS";
            case REQUEST_SWITCH_CONTEXT -> intrName = "REQUEST_SWITCH_CONTEXT";
            case REQUEST_TERMINATE_PROCESS -> intrName = "REQUEST_TERMINATE_PROCESS";
            case REQUEST_KILL_PROCESS -> intrName = "REQUEST_KILL_PROCESS";
            case REQUEST_PAGES -> intrName = "REQUEST_PAGES";
            case RESPONSE_PAGES -> intrName = "RESPONSE_PAGES";
            case OUT_OF_MEMORY -> intrName = "OUT_OF_MEMORY";
            case REQUEST_MEMORY_WRITE -> intrName = "REQUEST_MEMORY_WRITE";
            case RESPONSE_MEMORY_WRITE -> intrName = "RESPONSE_MEMORY_WRITE";
            case REQUEST_FREE_PAGE -> intrName = "REQUEST_FREE_PAGE";
            case RESPONSE_FREE_PAGE -> intrName = "RESPONSE_FREE_PAGE";
            case REQUEST_FILE -> intrName = "REQUEST_FILE";
            case RESPONSE_FILE -> intrName = "RESPONSE_FILE";
            case FILE_NOT_FOUND -> intrName = "FILE_NOT_FOUND";
            case REQUEST_CHANGE_DIRECTORY -> intrName = "REQUEST_CHANGE_DIRECTORY";
            case REQUEST_IO_WRITE -> intrName = "REQUEST_IO_WRITE";
            case RESPONSE_IO_WRITE -> intrName = "RESPONSE_IO_WRITE";
            case COMPLETE_IO -> intrName = "COMPLETE_IO";
            default -> intrName = "Unknown";
        }
        return "SIRQ[" + intrName + "]";
    }

}
