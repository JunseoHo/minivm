package os.process_manager;

<<<<<<< HEAD
import cpu.CPUContext;
import os.memory_manager.Page;

import java.util.LinkedList;
import java.util.List;

public class Process {
    // Process control block
    public int id;
    public String name;
    public CPUContext cpuContext;
    public int codeBase;
    public int dataBase;
    public int stackBase;
    public int heapBase;
    public int codeSize;
    public int dataSize;
    public int stackSize;
    public int heapSize;
    public List<Integer> pageTable;

    public Process(int id, String name) {
        this.id = id;
        this.name = name;
        this.cpuContext = new CPUContext();
    }

    public CPUContext getCPUContext() {
        return cpuContext;
    }

    public void setCPUContext(CPUContext cpuContext) {
        this.cpuContext = cpuContext;
    }

    public void setCode(int base, int size) {
        codeBase = base;
        codeSize = size;
        cpuContext.CS = base;
    }

    public void setData(int base, int size) {
        dataBase = base;
        dataSize = size;
        cpuContext.DS = base;
    }

    public void setStack(int base, int size) {
        stackBase = base;
        stackSize = size;
        cpuContext.SS = base;
        cpuContext.SP = base;
    }

    public void setHeap(int base, int size) {
        heapBase = base;
        heapSize = size;
        cpuContext.HS = base;
    }

    public int size() {
        return codeSize + dataSize + stackSize + heapSize;
    }


=======
import os.memory_manager.Page;

public class Process {

    private final int id;
    private final Page codeSegment;
    private final Page dataSegment;
    private Context context = new Context();

    public Process(int id, Page codeSegment, Page dataSegment) {
        this.id = id;
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        context.CS = codeSegment.base();
        context.DS = dataSegment.base();
    }

    public int getId() {
        return id;
    }

    public Context save() {
        return context;
    }

    public void restore(Context context) {
        this.context = context;
    }

    public Page getCodeSegment() {
        return codeSegment;
    }

    public Page getDataSegment() {
        return dataSegment;
    }

    @Override
    public String toString() {
        return "Process[" + id + "]";
    }

>>>>>>> origin/master
}
