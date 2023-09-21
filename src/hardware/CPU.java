package hardware;

import main.MiniOSUtil;
import os.process_manager.Context;

public class CPU implements Runnable {

    // Special-purpose registers
    private int PC = 0;
    private int MAR = 0;
    private int MBR = 0;
    private int IR_OPCODE = 0;
    private int IR_OPERAND = 0;
    // General registers
    private int AC = 0;
    // Segment registers
    private int CS = 0;
    private int DS = 0;
    // Status registers
    private boolean isZero = false;
    // Interrupt registers
    private boolean timeSliceExpired = false;
    private boolean halt = false;
    // Associations
    private Memory memory = null;

    public void associate(Memory memory) {
        this.memory = memory;
    }

    public Context getContext() {
        Context context = new Context();
        context.PC = PC;
        context.MAR = MAR;
        context.MBR = MBR;
        context.IR_OPCODE = IR_OPCODE;
        context.IR_OPERAND = IR_OPERAND;
        context.AC = AC;
        context.CS = CS;
        context.DS = DS;
        context.isZero = isZero;
        context.timeSliceExpired = timeSliceExpired;
        context.halt = halt;
        return context;
    }

    public String status() {
        return "[CPU Status]" +
                "\nPC               :" + PC +
                "\nMAR              :" + MAR +
                "\nMBR              :" + MBR +
                "\nIR_OPCODE        :" + IR_OPCODE +
                "\nIR_OPERAND       :" + IR_OPERAND +
                "\nAC               :" + AC +
                "\nCS               :" + CS +
                "\nDS               :" + DS +
                "\nisZero           :" + isZero +
                "\ntimeSliceExpired :" + timeSliceExpired +
                "\nhalt             :" + halt;
    }

    @Override
    public void run() {
        while (true) {
            MiniOSUtil.sleep(200);
            if (PC == 0) continue;
            fetch();
            decode();
            execute();
            checkInterrupt();
        }
    }

    private void fetch() {
        MAR = CS + PC;
        MBR = memory.read(MAR);
    }

    private void decode() {
        IR_OPCODE = MBR >> 29;
        IR_OPERAND = MBR & 0x1FFFFFFF;
    }

    private void execute() {
        switch (IR_OPCODE) {
            case 0x00 -> halt();
            case 0x01 -> load();
            case 0x02 -> store();
            case 0x03 -> jump();
            case 0x04 -> jumpZero();
            case 0x05 -> add();
            case 0x06 -> subtract();
            case 0x07 -> interrupt();
        }
    }

    private void checkInterrupt() {

    }

    private void halt() {
        halt = true;
    }

    private void load() {
        AC = memory.read(DS + IR_OPERAND);
    }

    private void store() {
        memory.write(DS + IR_OPERAND, AC);
    }

    private void jump() {
        PC = IR_OPERAND;
    }

    private void jumpZero() {
        if (isZero) jump();
    }

    private void add() {
        isZero = (AC += IR_OPERAND) == 0;
    }

    private void subtract() {
        isZero = (AC -= IR_OPERAND) == 0;
    }

    private void multiply() {
        isZero = (AC *= IR_OPERAND) == 0;
    }

    private void interrupt() {
        switch (IR_OPERAND) {

        }
    }

}
