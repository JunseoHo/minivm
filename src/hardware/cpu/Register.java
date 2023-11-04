package hardware.cpu;

import java.util.Arrays;

public class Register {

    private final Byte[] memories;

    public Register() {
        memories = new Byte[4];
        Arrays.fill(memories, 0);
    }

    public void set(int value) {
        memories[0] = (byte) ((value >> 24) & 0xFF);
        memories[1] = (byte) ((value >> 16) & 0xFF);
        memories[2] = (byte) ((value >> 8) & 0xFF);
        memories[3] = (byte) (value & 0xFF);
    }

    public int intValue() {
        int result = 0;
        for (Byte b : memories) result = (result << 8) + (int) b;
        return result;
    }

    public int intValue(int begin, int size) {
        int mask = 0;
        for (int bit = 0; bit < size; bit++) {
            mask <<= 1;
            ++mask;
        }
        return (intValue() >> (31 - begin)) & mask;
    }

    public void increment() {
        set(intValue() + 1);
    }


}
