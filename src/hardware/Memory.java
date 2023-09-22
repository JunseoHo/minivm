package hardware;

import java.util.Arrays;

public class Memory {

    // attributes
    private static final int SIZE = 512;
    private long[] memory = null;

    public Memory() {
        memory = new long[SIZE];
        Arrays.fill(memory, 0);
    }

    public long read(int addr) {
        return memory[addr];
    }

    public void write(int addr, long value) {
        memory[addr] = value;
    }

    public int size() {
        return SIZE;
    }

    public String status() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Memory Status]");
        for (int idx = 0; idx < SIZE; idx++)
            stringBuilder.append("\n").append("0x").append(Integer.toHexString(idx)).append(" : ").append(memory[idx]);
        return stringBuilder.toString();
    }
}
