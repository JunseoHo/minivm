package hardware.cpu;

import common.bus.Component;
import common.Utils;
import exception.ProcessorException;
import hardware.HIQ;
import hardware.HWName;
import os.SIQ;
import os.SWName;
import os.SystemCall;

public class CPU extends Component<HIQ> implements Runnable {
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
    // status registers
    private volatile boolean tasking = false; // Should be excluded from optimization
    // general-purpose registers
    private long AC = 0;
    // segment registers
    private long CS = 0;
    private long DS = 0;
    // components
    private final Timer timer = new Timer(500);
    // constants
    private static final int AM_IM = 0x00;
    private static final int AM_DI = 0x01;
    private static final int AM_ID = 0x02;

    public void associate(SystemCall systemCall) {
        this.systemCall = systemCall;
    }

    public Context save() {
        Context context = new Context();
        context.PC = PC;
        context.MAR = MAR;
        context.MBR = MBR;
        context.IR_ADDRESSING_MODE = IR_ADDRESSING_MODE;
        context.IR_OPCODE = IR_OPCODE;
        context.IR_OPERAND_L = IR_OPERAND_L;
        context.IR_OPERAND_R = IR_OPERAND_R;
        context.tasking = tasking;
        context.AC = AC;
        context.CS = CS;
        context.DS = DS;
        context.queue = queue;
        return context;
    }

    public void restore(Context context) {
        PC = context.PC;
        MAR = context.MAR;
        MBR = context.MBR;
        IR_ADDRESSING_MODE = context.IR_ADDRESSING_MODE;
        IR_OPCODE = context.IR_OPCODE;
        IR_OPERAND_L = context.IR_OPERAND_L;
        IR_OPERAND_R = context.IR_OPERAND_R;
        tasking = context.tasking;
        AC = context.AC;
        CS = context.CS;
        DS = context.DS;
        queue = context.queue;
    }

    public void switchTasking() {
        tasking = !tasking;
    }

    @Override
    public void run() {
        if (!POST()) return;
        systemCall.run();
        new Thread(timer).start();
        while (true) {
            handleInterrupt();
            if (!tasking) continue;
            fetch();
            ++PC;
            decode();
            execute();
            Utils.sleep(1000);
        }
    }

    private boolean POST() {
        if (!send(new HIQ(HWName.MEMORY, HIQ.STAT_CHK))) return false;
        if (!send(new HIQ(HWName.STORAGE, HIQ.STAT_CHK))) return false;
        if (receive(HIQ.STAT_POS, HIQ.STAT_NEG).id == HIQ.STAT_NEG) return false;
        if (receive(HIQ.STAT_POS, HIQ.STAT_NEG).id == HIQ.STAT_NEG) return false;
        return true;
    }

    private void fetch() {
        try {
            send(new HIQ(HWName.MEMORY, HIQ.REQUEST_READ, MAR = CS + PC));
            HIQ intr = receive(HIQ.RESPONSE_READ, HIQ.SEGFAULT);
            if (intr.id == HIQ.RESPONSE_READ) MBR = (Long) intr.values[0];
            else throw new ProcessorException("Segmentation fault.");
        } catch (ProcessorException e) {
            switchTasking();
        }
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
            case 0x04 -> sub();
            case 0x05 -> mul();
            case 0x06 -> jump();
            case 0x07 -> jumpZero();
            case 0x08 -> read();
            case 0x09 -> write();
            case 0x0A -> intr();
        }
    }

    private void halt() {
        queue.enqueue(new HIQ(HWName.CPU, HIQ.HALT));
    }

    private void load() {
        try {
            if (IR_ADDRESSING_MODE == AM_IM) AC = IR_OPERAND_R;
            else if (IR_ADDRESSING_MODE == AM_DI) {
                send(new HIQ(HWName.MEMORY, HIQ.REQUEST_READ, DS + IR_OPERAND_R));
                HIQ intr = receive(HIQ.RESPONSE_READ, HIQ.SEGFAULT);
                if (intr.id == HIQ.RESPONSE_READ) AC = (Long) intr.values[0];
                else throw new ProcessorException("Segmentation fault.");
            }
        } catch (ProcessorException e) {
            switchTasking();
        }
    }

    private void store() {
        try {
            send(new HIQ(HWName.MEMORY, HIQ.REQUEST_WRITE, DS + IR_OPERAND_R, AC));
            if (receive(HIQ.RESPONSE_WRITE, HIQ.SEGFAULT).id == HIQ.SEGFAULT)
                throw new ProcessorException("Segmentation fault.");
        } catch (ProcessorException e) {
            switchTasking();
        }
    }

    private void add() {
        try {
            if (IR_ADDRESSING_MODE == AM_IM) AC += IR_OPERAND_R;
            else if (IR_ADDRESSING_MODE == AM_DI) {
                send(new HIQ(HWName.MEMORY, HIQ.REQUEST_READ, DS + IR_OPERAND_R));
                HIQ intr = receive(HIQ.RESPONSE_READ, HIQ.SEGFAULT);
                if (intr.id == HIQ.RESPONSE_READ) AC += (Long) intr.values[0];
                else throw new ProcessorException("Segmentation fault.");
            }
        } catch (ProcessorException e) {
            switchTasking();
        }
    }

    private void sub() {
        try {
            if (IR_ADDRESSING_MODE == AM_IM) AC -= IR_OPERAND_R;
            else if (IR_ADDRESSING_MODE == AM_DI) {
                send(new HIQ(HWName.MEMORY, HIQ.REQUEST_READ, DS + IR_OPERAND_R));
                HIQ intr = receive(HIQ.RESPONSE_READ, HIQ.SEGFAULT);
                if (intr.id == HIQ.RESPONSE_READ) AC -= (Long) intr.values[0];
                else throw new ProcessorException("Segmentation fault.");
            }
        } catch (ProcessorException e) {
            switchTasking();
        }
    }

    private void mul() {
        try {
            if (IR_ADDRESSING_MODE == AM_IM) AC *= IR_OPERAND_R;
            else if (IR_ADDRESSING_MODE == AM_DI) {
                send(new HIQ(HWName.MEMORY, HIQ.REQUEST_READ, DS + IR_OPERAND_R));
                HIQ intr = receive(HIQ.RESPONSE_READ, HIQ.SEGFAULT);
                if (intr.id == HIQ.RESPONSE_READ) AC *= (Long) intr.values[0];
                else throw new ProcessorException("Segmentation fault.");
            }
        } catch (ProcessorException e) {
            switchTasking();
        }
    }

    private void jump() {
        PC = IR_OPERAND_R;
    }

    private void jumpZero() {
        if (IR_ADDRESSING_MODE == AM_IM) if (AC == 0) jump();
        if (IR_ADDRESSING_MODE == AM_DI) if (AC != 0) jump();
    }

    private void intr() {
        queue.enqueue(new HIQ(HWName.CPU, (int) IR_OPERAND_L, IR_OPERAND_R));
    }

    private void read() {

    }

    private void write() {
        // IR_OPERAND_L(13) -> PORT = (3), BASE = (10)
        int port = ((int) IR_OPERAND_L >> 10);
        int base = (int) (((int) IR_OPERAND_L & 0x3FF) + DS);
        int size = (int) IR_OPERAND_R;
        systemCall.generateInterrupt(new SIQ(SWName.PROCESS_MANAGER, SIQ.REQUEST_IO_WRITE, port, base, size));
        receive(HIQ.RESPONSE_IO_WRITE);
    }

    public void generateInterrupt(HIQ intr) {
        send(intr);
    }

    private void handleInterrupt() {
        for (HIQ intr : receiveAll()) queue.enqueue(intr);
        while (!queue.isEmpty()) {
            HIQ intr = queue.dequeue();
            switch (intr.id) {
                case HIQ.COMPLETE_IO -> {
                    int processId = (int) intr.values[0];
                    systemCall.generateInterrupt(new SIQ(SWName.PROCESS_MANAGER, SIQ.COMPLETE_IO, processId));
                    receive(HIQ.RESPONSE_TERMINATE);
                }
                case HIQ.TIME_SLICE_EXPIRED -> {
                    systemCall.generateInterrupt(new SIQ(SWName.PROCESS_MANAGER, SIQ.REQUEST_SWITCH_CONTEXT));
                    receive(HIQ.RESPONSE_SWITCH_CONTEXT);
                    timer.init();
                }
                case HIQ.HALT -> {
                    systemCall.generateInterrupt(new SIQ(SWName.PROCESS_MANAGER, SIQ.REQUEST_TERMINATE_PROCESS));
                    receive(HIQ.RESPONSE_TERMINATE);
                }
            }
        }
    }

    private class Timer implements Runnable {

        private int time = 0;
        private int clock;

        public void init() {
            time = 0;
        }

        public Timer(int clock) {
            this.clock = clock;
        }

        @Override
        public void run() {
            while (true) {
                if (++time > 2) queue.enqueue(new HIQ(HWName.MEMORY, HIQ.TIME_SLICE_EXPIRED));
                if (!Utils.sleep(clock)) return;
            }
        }
    }
}
