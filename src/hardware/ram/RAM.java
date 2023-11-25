package hardware.ram;

import hardware.IODevice;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RAM {

    private static final int size = 32768;
    private final Integer[] memories;
    private final Queue<String> histories;

    public RAM() {
        memories = new Integer[size];
        histories = new LinkedList<>();
    }

    public Integer read(int addr) {
        if (addr < 0 || addr > size - 1) return null;
        addHistory(String.format("%-12s%-20s%s", "READ, ", "Address : " + addr + ", ", "Value : " + memories[addr]));
        return memories[addr];
    }

    public Integer[] read(int addr, int size) {
        Integer[] values = new Integer[size];
        for (int i = 0; i < size; i++)
            if ((values[i] = read(addr + i)) == null) return null;
        return values;
    }

    public boolean write(int addr, Integer val) {
        if (addr < 0 || addr > size - 1) return false;
        memories[addr] = val;
        addHistory(String.format("%-12s%-20s%s", "WRITE, ", "Address : " + addr + ", ", "Value : " + memories[addr]));
        return true;
    }

    public boolean write(int addr, Integer[] val) {
        for (int i = 0; i < val.length; i++)
            if (!write(addr + i, val[i])) return false;
        return true;
    }

    public int size() {
        return size;
    }

    public Queue<String> getHistories() {
        return histories;
    }

    private void addHistory(String history) {
        while (histories.size() > 10) histories.poll();
        histories.add(history);
    }
}
