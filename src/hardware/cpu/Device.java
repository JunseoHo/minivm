package hardware.cpu;

import common.bus.Component;
import interrupt.IRQ;
import interrupt.ISR;

import java.util.LinkedList;
import java.util.List;

public abstract class Device extends Component<IRQ> {

    private final List<ISR> IVT = new LinkedList<>();

    protected void registerISR(ISR isr) {
        IVT.add(isr);
    }

}
