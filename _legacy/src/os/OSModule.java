package os;

import interrupt.bus.Component;
import interrupt.logger.MiniVMLogger;
import hardware.io_device.IODevice;

import java.util.HashMap;
import java.util.Map;

public abstract class OSModule extends Component<SIRQ> implements Runnable {

    private final Map<Integer, SW_ISR> interruptVectorTable = new HashMap<>();

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public void registerISR(int intrId, SW_ISR isr) {
        interruptVectorTable.put(intrId, isr);
    }

    public void handleInterrupt() {
        for (SIRQ intr : receiveAll()) queue.enqueue(intr);
        while (!queue.isEmpty()) {
            SIRQ intr = queue.dequeue();
            MiniVMLogger.info(name(), "Handle " + intr);
            interruptVectorTable.get(intr.id()).handle(intr);
        }
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }


}
