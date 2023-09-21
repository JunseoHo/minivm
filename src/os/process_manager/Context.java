package os.process_manager;

public class Context {

    // Special-purpose registers
    public long PC = 0;
    public long MAR = 0;
    public long MBR = 0;
    public long IR_OPCODE = 0;
    public long IR_OPERAND = 0;
    // General registers
    public long AC = 0;
    // Segment registers
    public long CS = 0;
    public long DS = 0;
    // Status registers
    public boolean isRunning = true;
    public boolean isZero = false;
    // Interrupt registers
    public boolean timeSliceExpired = false;
    public boolean halt = false;

}
