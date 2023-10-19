package hardware.cpu;

import common.CircularQueue;
import hardware.HIQ;

public class Context {

    public long PC = 0;
    public long MAR = 0;
    public long MBR = 0;
    public long IR_ADDRESSING_MODE = 0;
    public long IR_OPCODE = 0;
    public long IR_OPERAND_L = 0;
    public long IR_OPERAND_R = 0;
    public boolean tasking = true;
    public long AC = 0;
    public long CS = 0;
    public long DS = 0;
    public CircularQueue<HIQ> queue = new CircularQueue<>(100);

}
