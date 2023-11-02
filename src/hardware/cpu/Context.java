package hardware.cpu;

public class Context {

    public int PC;
    public int MAR;
    public int MBR;
    public long IR_AM = 0;
    public long IR_OPC = 0;
    public long IR_REG = 0;
    public long IR_OPR_L = 0;
    public long IR_OPR_R = 0;
    // status registers
    public volatile boolean IDLE = true;
    public volatile boolean ZERO = false;
    public volatile boolean NEGATIVE = false;
    // general-purpose registers
    public long EAX = 0;
    public long EBX = 0;
    public long ECX = 0;
    public long EDX = 0;
    // segment registers
    public long CS = 0;
    public long DS = 0;
    public long SS = 0;
    public long SP = 0;
    public long HS = 0;

    @Override
    public String toString() {
        String format = "%-12s : %s%n";
        return "CPU Context\n+----------------------+\n"
                + String.format(format, "PC", PC)
                + String.format(format, "MAR", MAR)
                + String.format(format, "MBR", MBR)
                + String.format(format, "IR_AM", IR_AM)
                + String.format(format, "IR_OPC", IR_OPC)
                + String.format(format, "IR_REG", IR_REG)
                + String.format(format, "IR_OPR_L", IR_OPR_L)
                + String.format(format, "IR_OPR_R", IR_OPR_R)
                + String.format(format, "IDLE", IDLE)
                + String.format(format, "ZERO", ZERO)
                + String.format(format, "NEGATIVE", NEGATIVE)
                + String.format(format, "EAX", EAX)
                + String.format(format, "EBX", EBX)
                + String.format(format, "ECX", ECX)
                + String.format(format, "EDX", EDX)
                + String.format(format, "CS", CS)
                + String.format(format, "DS", DS)
                + String.format(format, "SS", SS)
                + String.format(format, "SP", SP)
                + String.format(format, "HS", HS);
    }

}
