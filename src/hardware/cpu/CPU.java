package hardware.cpu;

import common.InterruptServiceRoutine;
import common.bus.Component;
import common.Utils;
import common.logger.MiniVMLogger;
import hardware.HIRQ;
import hardware.HWName;
import hardware.HW_ISR;
import os.SIRQ;
import os.SWName;
import os.SystemCall;

import java.util.HashMap;
import java.util.Map;

public class CPU extends Component<HIRQ> implements Runnable {
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
    private volatile boolean POWER_ON = true;
    private volatile boolean IDLE = true;
    // general-purpose registers
    private long AC = 0;
    // segment registers
    private long CS = 0;
    private long DS = 0;
    // components
    private final Timer timer = new Timer(500);
    private final Map<Integer, HW_ISR> interruptVectorTable = new HashMap<>();
    // addressing mode
    private static final int AM_IM = 0x00;
    private static final int AM_DI = 0x01;
    private static final int AM_ID = 0x02;
    // opcode
    private static final int OP_HLT = 0x00;
    private static final int OP_LDA = 0x01;
    private static final int OP_STO = 0x02;
    private static final int OP_ADD = 0x03;
    private static final int OP_SUB = 0x04;
    private static final int OP_MUL = 0x05;
    private static final int OP_JMP = 0x06;
    private static final int OP_JPZ = 0x07;
    private static final int OP_RDM = 0x08;
    private static final int OP_WRM = 0x09;
    private static final int OP_INT = 0x0A;

    public CPU() {
        registerISR(HIRQ.COMPLETE_IO, this::COMPLETE_IO);
        registerISR(HIRQ.TIME_SLICE_EXPIRED, this::TIME_SLICE_EXPIRED);
        registerISR(HIRQ.HALT, this::HALT);
    }

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
        context.IDLE = IDLE;
        context.AC = AC;
        context.CS = CS;
        context.DS = DS;
        context.queue = queue;
        return context;
    }

    public void restore(Context context) {
        if (context == null) return;
        PC = context.PC;
        MAR = context.MAR;
        MBR = context.MBR;
        IR_ADDRESSING_MODE = context.IR_ADDRESSING_MODE;
        IR_OPCODE = context.IR_OPCODE;
        IR_OPERAND_L = context.IR_OPERAND_L;
        IR_OPERAND_R = context.IR_OPERAND_R;
        IDLE = context.IDLE;
        AC = context.AC;
        CS = context.CS;
        DS = context.DS;
        queue = context.queue;
    }

    public void switchStatus(boolean stat) {
        IDLE = stat;
    }

    public void generateIntr(HIRQ intr) {
        send(intr);
    }

    @Override
    public void run() {
        new Thread(timer).start();
        systemCall.run();
        while (true) {
            handleInterrupt();
            if (!POWER_ON) break;
            if (IDLE) continue;
            fetch();
            ++PC;
            decode();
            execute();
            Utils.sleep(150); // down clock
        }
    }

    private void registerISR(int intrId, HW_ISR isr) {
        if (intrId < 0 || isr == null) MiniVMLogger.warn("CPU", "Invalid interrupt service routine is ignored.");
        else interruptVectorTable.put(intrId, isr);
    }

    private void handleInterrupt() {
        for (HIRQ intr : receiveAll()) enqueue(intr);
        while (!isEmpty()) {
            HIRQ intr = dequeue();
            HW_ISR isr = interruptVectorTable.get(intr.id());
            if (isr != null) isr.handle(intr);
        }
    }

    private void fetch() {
        send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, MAR = CS + PC, HWName.CPU));
        HIRQ intr = receive(HIRQ.RESPONSE_READ, HIRQ.SEGMENTATION_FAULT);
        if (intr.id() == HIRQ.RESPONSE_READ) MBR = (Long) intr.values()[0];
        else enqueue(new HIRQ(HWName.CPU, HIRQ.HALT)); // segmentation fault
    }

    private void decode() {
        IR_ADDRESSING_MODE = MBR >> 30;
        IR_OPCODE = (MBR & 0x3C000000) >> 26;
        IR_OPERAND_L = (MBR & 0x3FFE000) >> 13;
        IR_OPERAND_R = MBR & 0x1FFF;
    }

    private void execute() {
        switch ((int) IR_OPCODE) {
            case OP_HLT -> HLT();
            case OP_LDA -> LDA();
            case OP_STO -> STO();
            case OP_ADD -> ADD();
            case OP_SUB -> SUB();
            case OP_MUL -> MUL();
            case OP_JMP -> JMP();
            case OP_JPZ -> JPZ();
            case OP_RDM -> RDM();
            case OP_WRM -> WRM();
            case OP_INT -> INT();
        }
    }

    private void HLT() {
        enqueue(new HIRQ(HWName.CPU, HIRQ.HALT));
    }

    private void LDA() {
        if (IR_ADDRESSING_MODE == AM_IM) AC = IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == AM_DI) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, DS + IR_OPERAND_R, HWName.CPU));
            HIRQ intr = receive(HIRQ.RESPONSE_READ, HIRQ.SEGMENTATION_FAULT);
            if (intr.id() == HIRQ.RESPONSE_READ) AC = (Long) intr.values()[0];
            else new HIRQ(HWName.CPU, HIRQ.HALT); // segmentation fault
        }
    }

    private void STO() {
        send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_WRITE, DS + IR_OPERAND_R, AC, HWName.CPU));
        HIRQ intr = receive(HIRQ.RESPONSE_WRITE, HIRQ.SEGMENTATION_FAULT);
        if (intr.id() == HIRQ.SEGMENTATION_FAULT) new HIRQ(HWName.CPU, HIRQ.HALT); // segmentation fault
    }

    private void ADD() {
        if (IR_ADDRESSING_MODE == AM_IM) AC += IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == AM_DI) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, DS + IR_OPERAND_R, HWName.CPU));
            HIRQ intr = receive(HIRQ.RESPONSE_READ, HIRQ.SEGMENTATION_FAULT);
            if (intr.id() == HIRQ.RESPONSE_READ) AC += (Long) intr.values()[0];
            else new HIRQ(HWName.CPU, HIRQ.HALT); // segmentation fault
        }
    }

    private void SUB() {
        if (IR_ADDRESSING_MODE == AM_IM) AC -= IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == AM_DI) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, DS + IR_OPERAND_R, HWName.CPU));
            HIRQ intr = receive(HIRQ.RESPONSE_READ, HIRQ.SEGMENTATION_FAULT);
            if (intr.id() == HIRQ.RESPONSE_READ) AC -= (Long) intr.values()[0];
            else new HIRQ(HWName.CPU, HIRQ.HALT); // segmentation fault
        }
    }

    private void MUL() {
        if (IR_ADDRESSING_MODE == AM_IM) AC *= IR_OPERAND_R;
        else if (IR_ADDRESSING_MODE == AM_DI) {
            send(new HIRQ(HWName.MEMORY, HIRQ.REQUEST_READ, DS + IR_OPERAND_R, HWName.CPU));
            HIRQ intr = receive(HIRQ.RESPONSE_READ, HIRQ.SEGMENTATION_FAULT);
            if (intr.id() == HIRQ.RESPONSE_READ) AC *= (Long) intr.values()[0];
            else new HIRQ(HWName.CPU, HIRQ.HALT); // segmentation fault
        }
    }

    private void JMP() {
        PC = IR_OPERAND_R;
    }

    private void JPZ() {
        if (IR_ADDRESSING_MODE == AM_IM) if (AC == 0) JMP();
        if (IR_ADDRESSING_MODE == AM_DI) if (AC != 0) JMP();
    }

    private void INT() {
        queue.enqueue(new HIRQ(HWName.CPU, (int) IR_OPERAND_L, IR_OPERAND_R));
    }

    private void RDM() {
        /* not implemented yet */
    }

    private void WRM() {
        int port = ((int) IR_OPERAND_L >> 10);
        int base = (int) (((int) IR_OPERAND_L & 0x3FF) + DS);
        int size = (int) IR_OPERAND_R;
        systemCall.generateIntr(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_IO_WRITE, port, base, size));
        receive(HIRQ.RESPONSE_WRITE);
    }

    @InterruptServiceRoutine
    private void COMPLETE_IO(HIRQ intr) {
        int processId = (int) intr.values()[0];
        systemCall.generateIntr(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.COMPLETE_IO, processId));
        receive(HIRQ.RESPONSE_TERMINATE);
    }

    @InterruptServiceRoutine
    private void TIME_SLICE_EXPIRED(HIRQ intr) {
        systemCall.generateIntr(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_SWITCH_CONTEXT));
        receive(HIRQ.RESPONSE_TERMINATE);
        timer.init();
    }

    @InterruptServiceRoutine
    private void HALT(HIRQ intr) {
        systemCall.generateIntr(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.REQUEST_TERMINATE_PROCESS));
        receive(HIRQ.RESPONSE_TERMINATE);
    }

    private class Timer implements Runnable {

        private int time = 0;
        private final int clock;

        public void init() {
            time = 0;
        }

        public Timer(int clock) {
            this.clock = clock;
        }

        @Override
        public void run() {
            while (true) {
                if (++time > 2) queue.enqueue(new HIRQ(HWName.MEMORY, HIRQ.TIME_SLICE_EXPIRED));
                if (!Utils.sleep(clock)) return;
            }
        }
    }
}
