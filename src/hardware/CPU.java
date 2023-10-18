package hardware;

import common.CircularQueue;
import common.Component;
import common.Utils;
import os.SystemCall;

import java.util.ArrayList;
import java.util.List;
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
    // general-purpose registers
    private long AC = 0;
    // segment registers
    private long CS = 0;
    private long DS = 0;
    // components
    private boolean tasking = false;
    private Timer timer = new Timer();
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

    public List<Object> getContext() {
        List<Object> context = new ArrayList<>();
        context.add(PC);
        context.add(MAR);
        context.add(MBR);
        context.add(IR_ADDRESSING_MODE);
        context.add(IR_OPCODE);
        context.add(IR_OPERAND_L);
        context.add(IR_OPERAND_R);
        context.add(AC);
        context.add(CS);
        context.add(DS);
        context.add(interruptQueue);
        context.add(interrupt);
        return context;
    }

    public void setContext(List<Object> context) {
        PC = (long) context.get(0);
        MAR = (long) context.get(1);
        MBR = (long) context.get(2);
        IR_ADDRESSING_MODE = (long) context.get(3);
        IR_OPCODE = (long) context.get(4);
        IR_OPERAND_L = (long) context.get(5);
        IR_OPERAND_R = (long) context.get(6);
        AC = (long) context.get(7);
        CS = (long) context.get(8);
        DS = (long) context.get(9);
        interruptQueue = (CircularQueue<HWInterrupt>) context.get(10);
        interrupt = (HWInterrupt) context.get(11);
    }

    @Override
    public void run() {
        if (!powerOnSelfTest()) {
            System.err.println("POST failed.");
            return;
        } else System.out.println("POST OK.");
        systemCall.run();
        new Thread(timer).start();
        while (true) {
            if (!tasking) continue;
            fetch();
            decode();
            execute();
            handleInterrupt();
        }
    }

    public void switchTasking() {
        tasking = !tasking;
    }

    private void fetch() {
        send(new HWInterrupt("Memory", 0x03, MAR = CS + (PC++)));
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
        System.out.println((CS + PC) + " " + IR_OPCODE + " " + IR_OPCODE + " " + IR_OPERAND_L + " " + IR_OPERAND_R);
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
                case 0x08 -> {
                    systemCall.switchContext();
                    timer.init();
                }
            }
        }
    }

    private class Timer implements Runnable {

        private int sec = 0;

        public void init() {
            sec = 0;
        }

        @Override
        public void run() {
            while (true) {
                if (++sec > 2) interruptQueue.enqueue(new HWInterrupt("CPU", 0x08));
                if (!Utils.sleep(500)) return;
            }
        }
    }
}
