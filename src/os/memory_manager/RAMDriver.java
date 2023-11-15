package os.memory_manager;

import hardware.ram.RAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RAMDriver {
    // attributes
    public static final int FRAME_SIZE = 32;
    // hardware
    private RAM ram;
    // components
    public Integer[] frameTable;

    public RAMDriver(RAM ram) {
        // set associations
        this.ram = ram;
        // create components
        Arrays.fill(frameTable = new Integer[ram.size() / FRAME_SIZE], null);
    }

    public int allocate(int size) {
        int frameCount = size / FRAME_SIZE + 1;
        List<Integer> allocatedFrameIndexes = new LinkedList<>();
        for (int frameIndex = 0; frameIndex < frameTable.length; frameIndex++) {
            if (frameTable[frameIndex] == null) allocatedFrameIndexes.add(frameIndex);
            if (allocatedFrameIndexes.size() == frameCount) break;
        }
        if (allocatedFrameIndexes.size() < frameCount) return -1; // out of frame
        for (int index = 0; index < allocatedFrameIndexes.size(); index++) {
            if (index != allocatedFrameIndexes.size() - 1)
                frameTable[allocatedFrameIndexes.get(index)] = allocatedFrameIndexes.get(index + 1);
            else frameTable[allocatedFrameIndexes.get(index)] = -1;
        }
        return allocatedFrameIndexes.get(0);
    }

    public Byte read(int frameIndex, int offset) {
        return ram.read(frameIndex * FRAME_SIZE + offset);
    }

    public boolean write(int frameIndex, int offset, Byte val) {
        return ram.write(frameIndex * FRAME_SIZE + offset, val);
    }

}
