package os.file_manager;

import os.memory_manager.MemoryManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Executable {

    private final List<Long> instructions = new ArrayList<>();
    private final List<Long> data = new ArrayList<>();

    public Executable(File file) {
        List<Long> records = file.getRecords();
        for (int index = 0; index < 64; index++) instructions.add(records.get(index));
        for (int index = 64; index < 128; index++) data.add(records.get(index));
    }

    public List<Long> getInstructions() {
        return instructions;
    }

    public List<Long> getData() {
        return data;
    }

}
