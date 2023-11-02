package hardware.cpu;

import common.MiniVMUtils;
import interrupt.IRQ;
import interrupt.Intr;
import interrupt.ModuleName;
import main.MiniVM;
import os.SystemCall;

public class IA32 extends CPU {

    private final Timer timer = new Timer(500);

    public IA32() {
        registerOperation(Opcode.LDA, this::LDA);
    }

    private void LDA() {

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
                if (++time > 2) queue.enqueue(new IRQ(Intr.TIME_SLICE_EXPIRED, ModuleName.CPU));
                if (!MiniVMUtils.sleep(clock)) return;
            }
        }
    }

}
