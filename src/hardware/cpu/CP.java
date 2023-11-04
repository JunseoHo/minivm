package hardware.cpu;

import hardware.ram.RAM;

public class CP implements Runnable {
    // associations
    private RAM memory = null;
    // special-purpose registers
    private Register PC = new Register();
    private Register MAR = new Register();
    private Register MBR = new Register();
    private Register IR = new Register();
    private Register IR_AM = new Register();
    private Register IR_OPC = new Register();
    private Register IR_REG = new Register();
    private Register IR_OPR_0 = new Register();
    private Register IR_OPR_1 = new Register();
    private Register SR = new Register();
    private Register INT = new Register();
    // general-purpose registers
    private Register EAX = new Register();
    private Register EBX = new Register();
    private Register ECX = new Register();
    private Register EDX = new Register();
    // segment registers
    private Register CS = new Register();
    private Register DS = new Register();
    private Register SS = new Register();
    private Register SP = new Register();
    private Register HS = new Register();

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
            fetch();
            decode();
            execute();
        }
    }

    private void handleInterrupt() {

    }

    private void fetch() {
        MAR.set(PC.intValue());
        PC.increment();
        MBR.set(memory.read(MAR.intValue()));
        IR.set(MBR.intValue());
    }

    private void decode() {
        IR_AM.set(IR.intValue(0, 2));
        IR_OPC.set(IR.intValue(2, 4));
        IR_REG.set(IR.intValue(6, 4));
        IR_OPR_0.set(IR.intValue(10, 11));
        IR_OPR_1.set(IR.intValue(21, 11));
        if (IR_AM.intValue() == 0x01) IR_OPR_0.set(memory.read(IR_OPR_0.intValue())); // direct mode
    }

    private void execute() {

    }

}
