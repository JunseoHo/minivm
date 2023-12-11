package common.bus;

public class SWInterrupt extends Interrupt {
    public SWInterrupt(String receiver, int id, Object... values) {
        super(receiver, id, values);
    }

    public static final String PM = "ProcessManager";
    public static final String MM = "MemoryManager";
    public static final String FM = "FileManager";

    public static final int REQUEST_READ_MEMORY = 0;
    public static final int RESPONSE_READ_MEMORY = 1;
    public static final int REQUEST_WRITE_MEMORY = 2;
    public static final int RESPONSE_WRITE_MEMORY = 3;
    public static final int SEGMENTATION_FAULT = 4;
    public static final int REQUEST_ALLOCATE_MEMORY = 5;
    public static final int RESPONSE_ALLOCATE_MEMORY = 6;
    public static final int REQUEST_FREE_MEMORY = 7;
    public static final int RESPONSE_FREE_MEMORY = 8;
    public static final int OUT_OF_MEMORY = 9;
}
