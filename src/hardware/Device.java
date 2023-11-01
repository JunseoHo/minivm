package hardware;

import interrupt.ISR;

import java.util.LinkedList;
import java.util.List;

public abstract class Device {

    private final List<ISR> IVT = new LinkedList<>();

    protected void registerISR(ISR isr) {
        IVT.add(isr);
    }

}
