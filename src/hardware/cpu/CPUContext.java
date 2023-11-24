package hardware.cpu;

public class CPUContext {
    // special-purpose registers
    public int PC = 0;
    public int MAR = 0;
    public int MBR = 0;
    public int IR_AM = 0;
    public int IR_OPC = 0;
    public int IR_REG = 0;
    public int IR_OPR0 = 0;
    public int IR_OPR1 = 0;
    public int ST = 0;
    public int INT = 0;
    // segment registers
    public int CS = 0;
    public int DS = 0;
    public int SS = 0;
    public int SP = 0;
    public int HS = 0;
    public int HP = 0;
    // general-purpose registers
    public int AX = 0;
    public int BX = 0;
    public int CX = 0;
    public int DX = 0;
}
