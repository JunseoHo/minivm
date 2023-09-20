package hardware;

import java.util.Arrays;

public class Memory {

    private static final int SIZE = 256;
    private int[] memory;

    public Memory() {
        this.memory = new int[SIZE];
        Arrays.fill(this.memory, 0);
    }

    public int read(int addr) {
        if (addr < 0 || addr > SIZE - 1) return -1;
        return this.memory[addr];
    }

    public void write(int addr, int value) {
        if (addr > -1 && addr < SIZE) this.memory[addr] = value;
    }

    public String getStatus() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[Memory Status]");
        for (int idx = 0; idx < SIZE; idx++)
            stringBuilder.append("\n0x")
                    .append(Integer.toHexString(idx))
                    .append(" : ")
                    .append(memory[idx]);
        return stringBuilder.toString();
    }

}
