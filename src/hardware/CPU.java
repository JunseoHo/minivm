package hardware;

import common.Bus;
import common.Component;
import hardware.interrupt.IOInterrupt;

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
        if (!send(new IOInterrupt("Memory", 0))
                || !send(new IOInterrupt("Storage", 0))) {
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
