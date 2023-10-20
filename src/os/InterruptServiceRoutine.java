package os;

import common.bus.Event;

public interface InterruptServiceRoutine {

    void processInterrupt(SIQ intr);

}
