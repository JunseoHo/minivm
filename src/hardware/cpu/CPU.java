package hardware.cpu;

import common.MiniVMUtils;
import os.OperatingSystem;

public class CPU implements Runnable {
    // associations
    private OperatingSystem os;
    // special-purpose registers
    private int PC = 0;
    private int MAR = 0;
    private int MBR = 0;
    /*
        [Instruction Set Architecture]
        +-----------------------+--------------+---------------+-----------------+-----------------+
        | Addressing mode 1bit  | Opcode 5bit  | Register 2bit | Operand0 12bit  | Operand1 12bit  |
        +-----------------------+--------------+---------------+-----------------+-----------------+
     */
    private int IR_AM = 0; // use 1bit
    private int IR_OPC = 0; // use 5bit
    private int IR_REG = 0; // use 2bit
    private int IR_OPR0 = 0; // use 12bit
    private int IR_OPR1 = 0; // use 12bit
    /*
       [Status Register Field]
       +-----------+---------------+
       | Zero 1bit | Negative 1bit |
       +-----------+---------------+
    */
    private int ST = 0;
    /*
        [Interrupt Register Field]
        +------+--------------------+
        | Halt | Time Slice Expired |
        +------+--------------------+
     */
    private int INT = 0;
    // segment registers
    private int CS = 0;
    private int DS = 0;
    private int SS = 0;
    private int SP = 0;
    private int HS = 0;
    private int HP = 0;
    // general-purpose registers
    private int AX = 0;
    private int BX = 0;
    private int CX = 0;
    private int DX = 0;
    // components
    private Timer timer = new Timer(500);

    public void associate(OperatingSystem os) {
        this.os = os;
    }

    @Override
    public void run() {
        new Thread(timer).start();
        while (true) {
            handleInterrupt();
            fetch();
            decode();
            execute();
            MiniVMUtils.sleep(100); // under-clock
        }
    }

    private void handleInterrupt() {
        if (eval(INT, 0)) os.terminate();
        if (eval(INT, 1)) os.switchContext();
        INT = 0;
    }

    private void fetch() {
        MAR = CS + (PC++);
        MBR = os.readMemory(MAR);
    }

    private void decode() {
        IR_AM = MBR >> 31;
        IR_OPC = (MBR >> 26) & 31;
        IR_REG = (MBR >> 24) & 3;
        IR_OPR0 = (MBR >> 12) & 4095;
        IR_OPR1 = MBR & 4095;
    }

    private void execute() {
        switch (IR_OPC) {
            case 0x00 -> HLT(); // halt
            case 0x01 -> LDA(); // load
            case 0x02 -> STO(); // store
            case 0x03 -> ADD(); // add
            case 0x04 -> SUB(); // subtract
            case 0x05 -> MUL(); // multiply
            case 0x06 -> DIV(); // divide
            case 0x07 -> JMP(); // jump
            case 0x08 -> JPZ(); // jump zero
            case 0x09 -> JPN(); // jump negative
            case 0x0A -> INT(); // interrupt
            case 0x0B -> PSH(); // push
            case 0x0C -> POP(); // pop
            case 0x0D -> ALC(); // allocate
            case 0x0E -> FRE(); // free
        }
    }

    private void HLT() {
        INT = bitSet(INT, 0, true);
    }

    private void LDA() {
        if (IR_AM == 0x00) AX = IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> AX = os.readMemory(DS + IR_OPR1);
                case 0x01 -> AX = os.readMemory(SP + IR_OPR1);
                case 0x02 -> AX = os.readMemory(HS + IR_OPR1);
            }
        }
    }

    private void STO() {
        switch (IR_OPR0) {
            case 0x00 -> os.writeMemory(DS + IR_OPR1, AX);
            case 0x01 -> os.writeMemory(SP + IR_OPR1, AX);
            case 0x02 -> os.writeMemory(HS + IR_OPR1, AX);
        }
    }

    private void ADD() {
        if (IR_AM == 0x00) AX += IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> AX += os.readMemory(DS + IR_OPR1);
                case 0x01 -> AX += os.readMemory(SP + IR_OPR1);
                case 0x02 -> AX += os.readMemory(HS + IR_OPR1);
            }
        }
        ST = bitSet(ST, 0, AX == 0);
        ST = bitSet(ST, 1, AX < 0);
    }

    private void SUB() {
        if (IR_AM == 0x00) AX -= IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> AX -= os.readMemory(DS + IR_OPR1);
                case 0x01 -> AX -= os.readMemory(SP + IR_OPR1);
                case 0x02 -> AX -= os.readMemory(HS + IR_OPR1);
            }
        }
        ST = bitSet(ST, 0, AX == 0);
        ST = bitSet(ST, 1, AX < 0);
    }

    private void MUL() {
        if (IR_AM == 0x00) AX *= IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> AX *= os.readMemory(DS + IR_OPR1);
                case 0x01 -> AX *= os.readMemory(SP + IR_OPR1);
                case 0x02 -> AX *= os.readMemory(HS + IR_OPR1);
            }
        }
        ST = bitSet(ST, 0, AX == 0);
        ST = bitSet(ST, 1, AX < 0);
    }

    private void DIV() {
        if (IR_AM == 0x00) AX /= IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> AX /= os.readMemory(DS + IR_OPR1);
                case 0x01 -> AX /= os.readMemory(SP + IR_OPR1);
                case 0x02 -> AX /= os.readMemory(HS + IR_OPR1);
            }
        }
        ST = bitSet(ST, 0, AX == 0);
        ST = bitSet(ST, 1, AX < 0);
    }

    private void JMP() {
        if (IR_AM == 0x00) PC = IR_OPR1;
        else {
            switch (IR_OPR0) {
                case 0x00 -> PC = os.readMemory(DS + IR_OPR1);
                case 0x01 -> PC = os.readMemory(SP + IR_OPR1);
                case 0x02 -> PC = os.readMemory(HS + IR_OPR1);
            }
        }
    }

    private void JPZ() {
        if (eval(ST, 0)) JMP();
    }

    private void JPN() {
        if (eval(ST, 1)) JMP();
    }

    private void INT() {
        INT = bitSet(INT, IR_OPR1, true);
    }

    private void PSH() {
        if (IR_AM == 0x00) os.writeMemory(SP++, IR_OPR1);
        else {
            switch (IR_OPR0) {
                case 0x00 -> os.writeMemory(SP++, os.readMemory(DS + IR_OPR1));
                case 0x01 -> os.writeMemory(SP++, os.readMemory(SP + IR_OPR1));
                case 0x02 -> os.writeMemory(SP++, os.readMemory(HS + IR_OPR1));
            }
        }
    }

    private void POP() {
        --SP;
    }

    private void ALC() {
        AX = os.malloc(IR_OPR1);
    }

    private void FRE() {
        os.free(AX);
    }

    private int bitSet(int val, int bitIdx, boolean b) {
        int mask = 1 << (31 - bitIdx);
        if (b) return val | mask;
        return val & (~mask);
    }

    private boolean eval(int val, int bitIdx) {
        return ((val >> (31 - bitIdx)) & 1) == 1;
    }

    public CPUContext getContext() {
        CPUContext cpuContext = new CPUContext();
        cpuContext.PC = PC;
        cpuContext.MAR = MAR;
        cpuContext.MBR = MBR;
        cpuContext.IR_AM = IR_AM;
        cpuContext.IR_OPC = IR_OPC;
        cpuContext.IR_OPR0 = IR_OPR0;
        cpuContext.IR_OPR1 = IR_OPR1;
        cpuContext.ST = ST;
        cpuContext.INT = INT;
        cpuContext.CS = CS;
        cpuContext.DS = DS;
        cpuContext.SS = SS;
        cpuContext.SP = SP;
        cpuContext.HS = HS;
        cpuContext.HP = HP;
        cpuContext.AX = AX;
        cpuContext.BX = BX;
        cpuContext.CX = CX;
        cpuContext.DX = DX;
        return cpuContext;
    }

    public void setContext(CPUContext context) {
        PC = context.PC;
        MAR = context.MAR;
        MBR = context.MBR;
        IR_AM = context.IR_AM;
        IR_OPC = context.IR_OPC;
        IR_OPR0 = context.IR_OPR0;
        IR_OPR1 = context.IR_OPR1;
        ST = context.ST;
        INT = context.INT;
        CS = context.CS;
        DS = context.DS;
        SS = context.SS;
        SP = context.SP;
        HS = context.HS;
        HP = context.HP;
        AX = context.AX;
        BX = context.BX;
        CX = context.CX;
        DX = context.DX;
    }

    private class Timer implements Runnable {

        private int time = 0;
        private final int clock;

        public void init() {
            time = 0;
        }

        public Timer(int clock) {
            this.clock = clock;
        }

        @Override
        public void run() {
            while (true) {
                if (++time > 2) {
                    INT = bitSet(INT, 1, true);
                    init();
                }
                if (!MiniVMUtils.sleep(clock)) return;
            }
        }
    }

}
