package hardware.cpu;

import os.SystemCall;

import java.util.HashMap;
import java.util.Map;

public abstract class CPU extends Device {
    // associations
    private SystemCall systemCall;
    // special-purpose registers
    protected int PC;
    protected int MAR;
    protected int MBR;
    protected long IR_AM = 0;
    protected long IR_OPC = 0;
    protected long IR_REG = 0;
    protected long IR_OPR_L = 0;
    protected long IR_OPR_R = 0;
    // status registers
    protected volatile boolean IDLE = true;
    protected volatile boolean ZERO = false;
    protected volatile boolean NEGATIVE = false;
    // general-purpose registers
    protected long EAX = 0;
    protected long EBX = 0;
    protected long ECX = 0;
    protected long EDX = 0;
    // segment registers
    protected long CS = 0;
    protected long DS = 0;
    protected long SS = 0;
    protected long SP = 0;
    protected long HS = 0;
    // components
    private final Map<Integer, Operation> operationMap = new HashMap<>();

    protected void registerOperation(Integer opcode, Operation operation) {
        operationMap.put(opcode, operation);
    }

    protected void operate(Integer opcode) {
        operationMap.get(opcode).operate();
    }

    public Context getContext() {
        Context context = new Context();
        context.PC = PC;
        context.MAR = MAR;
        context.MBR = MBR;
        context.IR_AM = IR_AM;
        context.IR_OPC = IR_OPC;
        context.IR_REG = IR_REG;
        context.IR_OPR_L = IR_OPR_L;
        context.IR_OPR_R = IR_OPR_R;
        context.IDLE = IDLE;
        context.ZERO = ZERO;
        context.NEGATIVE = NEGATIVE;
        context.EAX = EAX;
        context.EBX = EBX;
        context.ECX = ECX;
        context.EDX = EDX;
        context.CS = CS;
        context.DS = DS;
        context.SS = SS;
        context.SP = SP;
        context.HS = HS;
        return context;
    }

    public void setContext() {

    }

}
