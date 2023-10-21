package os;

import common.CircularQueue;
import common.bus.Component;
import hardware.cpu.CPU;
import hardware.io_device.IODevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OSModule extends Component<SIQ> implements Runnable {

    private Map<Integer, InterruptServiceRoutine> interruptVectorTable;

    public OSModule() {
        interruptVectorTable = new HashMap<>();
    }

    public void associate(CPU cpu) {
        /* DO NOTHING */
    }

    public void associate(IODevice ioDevice) {
        /* DO NOTHING */
    }

    public void registerInterruptServiceRoutine(int interruptId, InterruptServiceRoutine isr) {
        interruptVectorTable.put(interruptId, isr);
    }

    public void handleInterrupt() {
        for (SIQ intr : receiveAll()) queue.enqueue(intr);
        while (!queue.isEmpty()) {
            SIQ intr = queue.dequeue();
            interruptVectorTable.get(intr.id).processInterrupt(intr);
        }
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }



}
