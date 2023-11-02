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

    public String getContext() {
        return null;
    }

    public void setContext() {

    }

}
