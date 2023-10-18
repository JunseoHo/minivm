package os.process_manager;

import common.CircularQueue;
import hardware.HWInterrupt;
import os.memory_manager.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Process {
    // attributes
    private int id;
    private Page codeSegment;
    private Page dataSegment;
    // cpu context
    private long PC = 0;
    private long MAR = 0;
    private long MBR = 0;
    private long IR_ADDRESSING_MODE = 0;
    private long IR_OPCODE = 0;
    private long IR_OPERAND_L = 0;
    private long IR_OPERAND_R = 0;
    private long AC = 0;
    private long CS = 0;
    private long DS = 0;
    private CircularQueue<HWInterrupt> interruptQueue = new CircularQueue<>(100);
    private HWInterrupt interrupt;

    public Process(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPage(Page codeSegment, Page dataSegment) {
        this.codeSegment = codeSegment;
        this.dataSegment = dataSegment;
        CS = codeSegment.base;
        DS = dataSegment.base;
    }

    public List<Object> getContext() {
        List<Object> context = new ArrayList<>();
        context.add(PC);
        context.add(MAR);
        context.add(MBR);
        context.add(IR_ADDRESSING_MODE);
        context.add(IR_OPCODE);
        context.add(IR_OPERAND_L);
        context.add(IR_OPERAND_R);
        context.add(AC);
        context.add(CS);
        context.add(DS);
        context.add(interruptQueue);
        context.add(interrupt);
        return context;
    }

    public void setContext(List<Object> context) {
        PC = (long) context.get(0);
        MAR = (long) context.get(1);
        MBR = (long) context.get(2);
        IR_ADDRESSING_MODE = (long) context.get(3);
        IR_OPCODE = (long) context.get(4);
        IR_OPERAND_L = (long) context.get(5);
        IR_OPERAND_R = (long) context.get(6);
        AC = (long) context.get(7);
        CS = (long) context.get(8);
        DS = (long) context.get(9);
        interruptQueue = (CircularQueue<HWInterrupt>) context.get(10);
        interrupt = (HWInterrupt) context.get(11);
    }
}
