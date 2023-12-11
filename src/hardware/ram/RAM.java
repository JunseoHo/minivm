package hardware.ram;

import common.MiniVMUtils;
import common.SyncQueue;

import java.util.Arrays;

public class RAM {

    private static final int TOTAL_SIZE = 32768;
    private static final int FRAME_SIZE = 32;
    private final Integer[] memories;
    private Integer[] frameTable;
    private final SyncQueue<RamHistory> histories;

    public RAM() {
        memories = new Integer[TOTAL_SIZE];
        frameTable = new Integer[TOTAL_SIZE / FRAME_SIZE];
        Arrays.fill(frameTable, null);
        histories = new SyncQueue<>();
    }

    public static void main(String[] args) {
        RAM ram = new RAM();
        int one = ram.malloc(64);
        int two = ram.malloc(64);
        int thr = ram.malloc(64);
        ram.free(two);
        ram.free(one);
        ram.free(thr);
    }

    public Integer malloc(int size) {
        int required = (size / FRAME_SIZE) + (size % FRAME_SIZE == 0 ? 0 : 1);
        int start = 0;
        int end = 0;
        for (; end < frameTable.length; end++) {
            if (frameTable[end] != null) start = end + 1;
            else if (end - start + 1 == required) break;
        }
        if (end - start + 1 < required) return null;
        for (int i = 0; start + i <= end; i++) {
            if (start + i == end) frameTable[start + i] = -1;
            else frameTable[start + i] = start + i + 1;
        }
        return start * FRAME_SIZE;
    }

    public void free(int addr) {
        int frameIndex = addr / FRAME_SIZE;
        while (frameTable[frameIndex] != -1) {
            int next = frameTable[frameIndex];
            frameTable[frameIndex] = null;
            frameIndex = next;
        }
        frameTable[frameIndex] = null;
    }

    public Integer read(int addr) {
        if (addr < 0 || addr > TOTAL_SIZE - 1) return null;
        addHistory(new RamHistory(MiniVMUtils.READ, addr, memories[addr]));
        return memories[addr];
    }

    public Integer[] read(int addr, int size) {
        Integer[] values = new Integer[size];
        for (int i = 0; i < size; i++)
            if ((values[i] = read(addr + i)) == null) return null;
        return values;
    }

    public boolean write(int addr, Integer val) {
        if (addr < 0 || addr > TOTAL_SIZE - 1 || val == null) return false;
        memories[addr] = val;
        addHistory(new RamHistory(MiniVMUtils.WRITE, addr, val));
        return true;
    }

    public boolean write(int addr, Integer[] val) {
        for (int i = 0; i < val.length; i++)
            if (!write(addr + i, val[i])) return false;
        return true;
    }

    public int size() {
        return TOTAL_SIZE;
    }

    public SyncQueue<RamHistory> getHistories() {
        return histories;
    }

    private void addHistory(RamHistory history) {
        while (histories.size() > 10) histories.poll();
        histories.add(history);
    }
}
