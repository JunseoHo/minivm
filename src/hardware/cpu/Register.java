package hardware.cpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Register {

    private boolean[] bits;

    public Register() {
        bits = new boolean[32];
    }

    public void set(long value) {
        for (int i = 0; i < 32; i++) {
            bits[31 - i] = (value & 1) == 1;
            value >>= 1;
        }
    }

    public long value() {
        return value(0, 32);
    }

    public long value(int begin, int size) {
        long result = 0;
        for (int i = 0; i < size; i++) {
            result <<= 1;
            if (bits[begin + i]) result |= 1;
        }
        return result;
    }

    public long bit(int index) {
        return bits[index] ? 1 : 0;
    }

    public void increment() {
        set(value() + 1L);
    }


}
