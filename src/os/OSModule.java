package os;

import common.bus.Component;
import common.bus.SWInterrupt;
import hardware.IODevice;
import hardware.cpu.CPU;

import java.util.HashMap;
import java.util.Map;

public abstract class OSModule extends Component<SWInterrupt> implements Runnable {

    private final Map<Integer, SWInterruptServiceRoutine> interruptVectorTable = new HashMap<>();

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public void registerISR(int intrId, SWInterruptServiceRoutine isr) {
        interruptVectorTable.put(intrId, isr);
    }

    public void handleInterrupt() {
        for (SWInterrupt intr : receiveAll()) queue.add(intr);
        while (!queue.isEmpty()) {
            SWInterrupt intr = queue.poll();
            interruptVectorTable.get(intr.id()).handle(intr);
        }
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }
}
