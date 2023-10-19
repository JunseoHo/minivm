package os.process_manager;

import common.CircularQueue;
import hardware.HIQ;
import hardware.cpu.Context;
import os.memory_manager.Page;

import java.util.ArrayList;
import java.util.List;

public class Process {

    private int id;
    private Page codeSegment;
    private Page dataSegment;
    private Context context = new Context();

    public Process(int id) {
        this.id = id;
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

    public void setPage(Page codeSegment, Page dataSegment) {
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        context.CS = codeSegment.base;
        context.DS = dataSegment.base;
    }

}
