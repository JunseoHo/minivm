package hardware.cpu;

import common.CircularQueue;
import hardware.HIRQ;

public class Context {

    public long PC = 0;
    public long MAR = 0;
    public long MBR = 0;
    public long IR_ADDRESSING_MODE = 0;
    public long IR_OPCODE = 0;
    public long IR_OPERAND_L = 0;
    public long IR_OPERAND_R = 0;
    public boolean IDLE = true;
    public long AC = 0;
    public long CS = 0;
    public long DS = 0;
    public CircularQueue<HIRQ> queue = new CircularQueue<>();

    @Override
    public String toString() {
        return    "PC                   : 0x" + Long.toHexString(PC) + "\n"
                + "MAR                  : 0x" + Long.toHexString(MAR) + "\n"
                + "MBR                  : 0x" + Long.toHexString(MBR) + "\n"
                + "IR_ADDRESSING_MODE   : 0x" + Long.toHexString(IR_ADDRESSING_MODE) + "\n"
                + "IR_OPCODE            : 0x" + Long.toHexString(IR_OPCODE) + "\n"
                + "IR_OPERAND_L         : 0x" + Long.toHexString(IR_OPERAND_L) + "\n"
                + "IR_OPERAND_R         : 0x" + Long.toHexString(IR_OPERAND_R) + "\n"
                + "IDLE                 : " + IDLE + "\n"
                + "AC                   : 0x" + Long.toHexString(AC) + "\n"
                + "CS                   : 0x" + Long.toHexString(CS) + "\n"
                + "DS                   : 0x" + Long.toHexString(DS);
    }

}
