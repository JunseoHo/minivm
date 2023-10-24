package hardware.io_device;

import common.bus.Component;
import hardware.HIRQ;
import hardware.HWName;
import hardware.HardwareInterruptServiceRoutine;

import java.util.HashMap;
import java.util.Map;

public abstract class IODevice extends Component<HIRQ> implements Runnable {

    private Map<Integer, HardwareInterruptServiceRoutine> interruptVectorTable;

    public IODevice() {
        this.interruptVectorTable = new HashMap<>();
    }

    protected void registerInterruptServiceRoutine(int interruptId, HardwareInterruptServiceRoutine routine) {
        interruptVectorTable.put(interruptId, routine);
    }

    private void handleInterrupt() {
        HIRQ intr;
        if ((intr = receive()) == null) return;
        HardwareInterruptServiceRoutine routine = interruptVectorTable.get(intr.id());
        if (routine != null) routine.handle(intr);
    }

    @Override
    public void run() {
        while (true){
            for (HIRQ intr : receiveAll()) enqueue(intr);
            while (!isEmpty()) {
                HIRQ intr = dequeue();
                HardwareInterruptServiceRoutine routine = interruptVectorTable.get(intr.id());
                if (routine != null) routine.handle(intr);
            }
        }
    }

    public void generateIntr(HIRQ intr) {
        enqueue(intr);
    }

    public abstract void read(HIRQ intr);

    public abstract void write(HIRQ intr);

    public abstract int bufferSize();

}
