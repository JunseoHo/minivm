package hardware;

import main.MiniOSUtil;
import os.process_manager.Context;
import os.process_manager.ProcessManager;

public class CPU implements Runnable {

    // Special-purpose registers
    private long PC = 0;
    private long MAR = 0;
    private long MBR = 0;
    private long IR_OPCODE = 0;
    private long IR_OPERAND = 0;
    // General registers
    private long AC = 0;
    // Segment registers
    private long CS = 0;
    private long DS = 0;
    // Status registers
    public boolean isRunning = false;
    private boolean isZero = false;
    // Interrupt registers
    private boolean timeSliceExpired = false;
    private boolean halt = false;
    // Associations
    private Memory memory = null;
    private ProcessManager processManager = null;
    // Modules
    private Timer timer = null;

    public void associate(Memory memory, ProcessManager processManager) {
        this.memory = memory;
        this.processManager = processManager;
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
        context.isRunning = isRunning;
        context.isZero = isZero;
        context.timeSliceExpired = timeSliceExpired;
        context.halt = halt;
        return context;
    }

    public void setContext(Context context) {
        PC = context.PC;
        MAR = context.MAR;
        MBR = context.MBR;
        IR_OPCODE = context.IR_OPCODE;
        IR_OPERAND = context.IR_OPERAND;
        AC = context.AC;
        CS = context.CS;
        DS = context.DS;
        isRunning = context.isRunning;
        isZero = context.isZero;
        timeSliceExpired = context.timeSliceExpired;
        halt = context.halt;
    }

    public String status() {
        String stringBuilder = "[CPU Status]" +
                "\nPC               : " + PC +
                "\nMAR              : " + MAR +
                "\nMBR              : " + MBR +
                "\nIR_OPCODE        : " + IR_OPCODE +
                "\nIR_OPERAND       : " + IR_OPERAND +
                "\nAC               : " + AC +
                "\nCS               : " + CS +
                "\nDS               : " + DS +
                "\nisRunning        : " + isRunning +
                "\nisZero           : " + isZero +
                "\ntimeSliceExpired : " + timeSliceExpired +
                "\nhalt             : " + halt;
        return stringBuilder;
    }

    @Override
    public void run() {
        timer = new Timer();
        timer.start();
        while (true) {
            MiniOSUtil.sleep(200);
            if (!isRunning) continue;
            fetch();
            ++PC;
            decode();
            execute();
            checkInterrupt();
        }
    }

    private void fetch() {
        MAR = CS + PC;
        System.out.println(MAR);
        MBR = memory.read((int) MAR);
    }

    private void decode() {
        IR_OPCODE = MBR >> 29;
        IR_OPERAND = MBR & 0x1FFFFFFF;
    }

    private void execute() {
        switch ((int) IR_OPCODE) {
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
        if (halt) {
            processManager.release();
            halt = false;
            timer.init();
        } else if (timeSliceExpired) {
            processManager.contextSwitch();
            timeSliceExpired = false;
        }
    }

    private void halt() {
        halt = true;
    }

    private void load() {
        AC = memory.read((int) (DS + IR_OPERAND));
    }

    private void store() {
        memory.write((int) (DS + IR_OPERAND), AC);
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
        switch ((int) IR_OPERAND) {
            case 0x00:
                System.out.println("PRINT : " + AC);
        }
    }

    private class Timer extends Thread {

        private int sec = 0;

        public void init() {
            sec = 0;
            timeSliceExpired = false;
        }

        @Override
        public void run() {
            while (true) {
                ++this.sec;
                if (this.sec > 2) timeSliceExpired = true;
                MiniOSUtil.sleep(500);
            }
        }
    }

}
