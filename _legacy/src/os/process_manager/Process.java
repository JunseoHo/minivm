package os.process_manager;

import hardware.cpu.Context;
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

}
