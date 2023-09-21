package hardware;

import java.util.Arrays;

public class Memory {

    private static final int SIZE = 256;
    private int[] memory;

    public Memory() {
        memory = new int[SIZE];
        Arrays.fill(memory, 0);
    }

    public int read(int addr) {
        return memory[addr];
    }

    public void write(int addr, int value) {
        memory[addr] = value;
    }

    public String status() {
        StringBuilder status = new StringBuilder("[Memory Status]");
        for (int idx = 0; idx < SIZE; idx++)
            status.append("\n0x").append(Integer.toHexString(idx)).append(" : ").append(memory[idx]);
        return status.toString();
    }

}
