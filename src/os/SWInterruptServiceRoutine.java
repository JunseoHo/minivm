package os;

import common.bus.SWInterrupt;

public interface SWInterruptServiceRoutine {
    void handle(SWInterrupt intr);
}
