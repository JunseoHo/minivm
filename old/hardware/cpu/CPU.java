package hardware.cpu;

import bios.BIOS;
import hardware.memory.Memory;
import main.MiniOSUtil;

public class CPU implements Runnable {
    // special-purpose registers
    private long PC = 0;
    private long MAR = 0;
    private long MBR = 0;
    private long IR_ADDRESSING_MODE = 0; // 0x00 Immediate, 0x01 Direct, 0x02 Indirect
    private long IR_OPCODE = 0;
    private long IR_OPERAND_L = 0;
    private long IR_OPERAND_R = 0;
    // general registers
    private long AC = 0;
    // segment registers
    private long CS = 0;
    private long DS = 0;
    // status registers
    private boolean isRunning = false;
    private boolean isZero = false;
    // interrupt registers
    private boolean powerOff = false;
    private boolean halt = false;
    private boolean waitIO = false;
    private boolean timeSliceExpired = false;
    // hardware
    private Memory memory;
    // system
    private BIOS bios;
    // components
    private Timer timer;

    public void bindHardware(Memory memory) { this.memory = memory; }

    public void installSystem(BIOS bios) {
        this.bios = bios;
    }

    public String status() {
        return getContext().toString();
    }

    public Context getContext() {
        return new Context(PC, MAR, MBR, IR_ADDRESSING_MODE, IR_OPCODE, IR_OPERAND_L, IR_OPERAND_R,
                AC, CS, DS, isRunning, isZero, halt, waitIO, timeSliceExpired);
    }

    public void setContext(Context context) {
        PC = context.PC();
        MAR = context.MAR();
        MBR = context.MBR();
        IR_ADDRESSING_MODE = context.IR_ADDRESSING_MODE();
        IR_OPCODE = context.IR_OPCODE();
        IR_OPERAND_L = context.IR_OPERAND_L();
        IR_OPERAND_R = context.IR_OPERAND_R();
        AC = context.AC();
        CS = context.CS();
        DS = context.DS();
        isRunning = context.isRunning();
        isZero = context.isZero();
        halt = context.halt();
        waitIO = context.waitIO();
        timeSliceExpired = context.timeSliceExpired();
    }

    @Override
    public void run() {
        System.out.println("CPU power on.");
        (timer = new Timer()).start();
        new Thread(bios).start();
        while (true) {
            MiniOSUtil.sleep(200);
            if (powerOff) break;
            if (!isRunning) continue;
            fetch();
            decode();
            execute();
            checkInterrupt();
        }
        timer.interrupt();
        System.out.println("CPU power off.");
    }

    private void fetch() {
        MBR = memory.read((int) (MAR = CS + (PC++)));
    }

    private void decode() {
        IR_ADDRESSING_MODE = MBR >> 30;
        IR_OPCODE = (MBR & 0x3C000000) >> 26;
        IR_OPERAND_L = (MBR & 0x3FFE000) >> 13;
        IR_OPERAND_R = MBR & 0x1FFF;
    }

    private void execute() {
        switch ((int) IR_OPCODE) {
            case 0x00 -> halt();
            case 0x01 -> load();
            case 0x02 -> store();
            case 0x03 -> add();
            case 0x04 -> subtract();
            case 0x05 -> multiply();
            case 0x06 -> jump();
            case 0x07 -> jumpZero();
            case 0x08 -> read();
            case 0x09 -> write();
            case 0x0A -> interrupt();
        }
    }

    private void checkInterrupt() {

    }

    private void halt() {
        halt = true;
    }

    private void load() {
        if (IR_ADDRESSING_MODE == 0x00) AC = IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) AC = memory.read((int) (DS + IR_OPERAND_R));
    }

    private void store() {
        memory.write((int) (DS + IR_OPERAND_R), AC);
    }

    private void add() {
        if (IR_ADDRESSING_MODE == 0x00) isZero = (AC += IR_OPERAND_R) == 0;
        else if (IR_ADDRESSING_MODE == 0x01) isZero = (AC += memory.read((int) (DS + IR_OPERAND_R))) == 0;
    }

    private void subtract() {
        if (IR_ADDRESSING_MODE == 0x00) isZero = (AC -= IR_OPERAND_R) == 0;
        else if (IR_ADDRESSING_MODE == 0x01) isZero = (AC -= memory.read((int) (DS + IR_OPERAND_R))) == 0;
    }

    private void multiply() {
        if (IR_ADDRESSING_MODE == 0x00) isZero = (AC *= IR_OPERAND_R) == 0;
        else if (IR_ADDRESSING_MODE == 0x01) isZero = (AC *= memory.read((int) (DS + IR_OPERAND_R))) == 0;
    }

    private void jump() {
        if (IR_ADDRESSING_MODE == 0x00) PC = IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) PC = memory.read((int) (DS + IR_OPERAND_R));
    }

    private void jumpZero() {
        if (isZero) jump();
    }

    private void read() {
        //operatingSystem.readStdin((int) (DS + IR_OPERAND_L), (int) IR_OPERAND_R);
        waitIO = true;
    }

    private void write() {
        //operatingSystem.writeStdout((int) (DS + IR_OPERAND_L), (int) IR_OPERAND_R);
        waitIO = true;
    }

    private void interrupt() {

    }

    public void interrupt(String interruptName) {
        switch (interruptName) {
            case "PowerOff" -> powerOff = true;
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
                if (++sec > 2) timeSliceExpired = true;
                if (!MiniOSUtil.sleep(500)) return;
            }
        }
    }

}
