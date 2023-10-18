package hardware;

import common.CircularQueue;
import common.Component;
import os.OS;
import os.SystemCall;

import java.util.Timer;

public class CPU extends Component<HWInterrupt> implements Runnable {
    // associations
    private SystemCall systemCall;
    // special-purpose registers
    private long PC = 0;
    private long MAR = 0;
    private long MBR = 0;
    private long IR_ADDRESSING_MODE = 0;
    private long IR_OPCODE = 0;
    private long IR_OPERAND_L = 0;
    private long IR_OPERAND_R = 0;
    private long STAT = 0;
    private long INTR = 0;
    // general-purpose registers
    private long AC = 0;
    // segment registers
    private long CS = 0;
    private long DS = 0;
    // components
    private Timer timer;
    private CircularQueue<HWInterrupt> interruptQueue = new CircularQueue<>(100);
    private HWInterrupt interrupt;

    private boolean powerOnSelfTest() {
        if (!send(new HWInterrupt("Memory", 0x00))
                || !send(new HWInterrupt("Storage", 0x00))) {
            System.err.println("bus error.");
            return false;
        }
        int AHC = 0;
        while (AHC < 2) {
            while ((interrupt = receive()) == null) ;
            if (interrupt.id == 1) ++AHC;
            else if (interrupt.id == 2) return false;
        }
        return true;
    }

    public void associate(SystemCall systemCall) {
        this.systemCall = systemCall;
    }

    @Override
    public void run() {
        if (!powerOnSelfTest()) {
            System.err.println("POST failed.");
            return;
        } else System.out.println("POST OK.");
        systemCall.run();
        while (true) {
            fetch();
            decode();
            execute();
            handleInterrupt();
        }
    }

    private void fetch() {
        send(new HWInterrupt("Memory", 0x03, MAR = PC++));
        interrupt = receive();
        if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        else if (interrupt.id == 0x04) MBR = interrupt.values[0];
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
            case 0x0A -> intr();
        }
    }

    private void halt() {
        interruptQueue.enqueue(new HWInterrupt("CPU", 0x07));
    }

    private void load() {
        if (IR_ADDRESSING_MODE == 0x00) AC = IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) {
            send(new HWInterrupt("Memory", 0x03, DS + IR_OPERAND_R));
            interrupt = receive(0x04, 0x40);
            if (interrupt.id == 0x04) AC = interrupt.values[0];
            else if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        }
    }

    private void store() {
        send(new HWInterrupt("Memory", 0x05, DS + IR_OPERAND_R, AC));
        interrupt = receive(0x06, 0x40);
        if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
    }

    private void add() {
        if (IR_ADDRESSING_MODE == 0x00) AC += IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) {
            send(new HWInterrupt("Memory", 0x03, DS + IR_OPERAND_R));
            interrupt = receive(0x04, 0x40);
            if (interrupt.id == 0x04) AC += interrupt.values[0];
            else if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        }
    }

    private void subtract() {
        if (IR_ADDRESSING_MODE == 0x00) AC -= IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) {
            send(new HWInterrupt("Memory", 0x03, DS + IR_OPERAND_R));
            interrupt = receive(0x04, 0x40);
            if (interrupt.id == 0x04) AC -= interrupt.values[0];
            else if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        }
    }

    private void multiply() {
        if (IR_ADDRESSING_MODE == 0x00) AC *= IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) {
            send(new HWInterrupt("Memory", 0x03, DS + IR_OPERAND_R));
            interrupt = receive(0x04, 0x40);
            if (interrupt.id == 0x04) AC *= interrupt.values[0];
            else if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        }
    }

    private void jump() {
        if (IR_ADDRESSING_MODE == 0x00) PC = IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == 0x01) {
            send(new HWInterrupt("Memory", 0x03, DS + IR_OPERAND_R));
            interrupt = receive(0x04, 0x40);
            if (interrupt.id == 0x04) PC = interrupt.values[0];
            else if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        }
    }

    private void jumpZero() {
        if (AC == 0) jump();
    }

    private void intr() {
        interruptQueue.enqueue(new HWInterrupt("CPU", (int) IR_OPERAND_L, IR_OPERAND_R));
    }

    private void read() {

    }

    private void write() {

    }

    private HWInterrupt receive(int... interruptIds) {
        while (true) {
            interrupt = receive();
            for (int interruptId : interruptIds) if (interrupt.id == interruptId) return interrupt;
            interruptQueue.enqueue(interrupt);
        }
    }

    private void handleInterrupt() {
        for (HWInterrupt interrupt : receiveAll()) interruptQueue.enqueue(interrupt);
        while (!interruptQueue.isEmpty()) {
            interrupt = interruptQueue.dequeue();
            switch (interrupt.id) {

            }
        }
    }
}
