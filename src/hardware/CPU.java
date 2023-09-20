package hardware;

import os.process_manager.Context;

public class CPU extends Thread {

    // Special-purpose registers
    private int PC = 0;
    private int MAR = 0;
    private int MBR = 0;
    private int IR_ADDRESSING_MODE = 0;
    private int IR_OPERATOR = 0;
    private int IR_OPERAND_L = 0;
    private int IR_OPERAND_R = 0;
    // Segment registers
    private int CS = 0;
    private int DS = 0;
    // associations
    private Memory memory;

    public Context getContext() {
        Context context = new Context();
        context.PC = PC;
        return context;
    }

    public void associate(Memory memory) {
        this.memory = memory;
    }

    @Override
    public void run() {
        while (true) {
            fetch();
            decode();
            execute();
            checkInterrupt();
        }
    }

    private void fetch() {

    }

    private void decode() {

    }

    private void execute() {

    }

    private void checkInterrupt() {

    }

}
