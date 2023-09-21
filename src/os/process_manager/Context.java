package os.process_manager;

public class Context {

    // Special-purpose registers
    public int PC = 0;
    public int MAR = 0;
    public int MBR = 0;
    public int IR_OPCODE = 0;
    public int IR_OPERAND = 0;
    // General registers
    public int AC = 0;
    // Segment registers
    public int CS = 0;
    public int DS = 0;
    // Status registers
    public boolean isZero = false;
    // Interrupt registers
    public boolean timeSliceExpired = false;
    public boolean halt = false;

}
