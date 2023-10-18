package hardware;

import common.Component;

import java.util.List;
import java.util.Timer;

public class CPU extends Component<IOInterrupt> implements Runnable {
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
    private IOInterrupt interrupt;

    private boolean powerOnSelfTest() {
        if (!send(new IOInterrupt("Memory", 0x00))
                || !send(new IOInterrupt("Storage", 0x00))) {
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

    @Override
    public void run() {
        if (!powerOnSelfTest()) {
            System.err.println("POST failed.");
            return;
        } else System.out.println("POST OK.");
        while (true) {
            fetch();
            decode();
            execute();
            handleInterrupt();
        }
    }

    private void fetch() {
        send(new IOInterrupt("Memory", 0x03, MAR = PC++));
        interrupt = receive();
        if (interrupt.id == 0x40) System.err.println("Segmentation fault.");
        else if (interrupt.id == 0x04) MBR = interrupt.value;
    }

    private void decode() {
        IR_ADDRESSING_MODE = MBR >> 30;
        IR_OPCODE = (MBR & 0x3C000000) >> 26;
        IR_OPERAND_L = (MBR & 0x3FFE000) >> 13;
        IR_OPERAND_R = MBR & 0x1FFF;
    }

    private void execute() {
        System.out.println(IR_ADDRESSING_MODE + " " + IR_OPCODE + " " + IR_OPERAND_L + " " + IR_OPERAND_R);
    }

    private void handleInterrupt() {
        for (IOInterrupt interrupt : receiveAll()) {
            switch (interrupt.id) {

            }
        }
    }
}
