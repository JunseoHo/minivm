package hardware;

public class CPU {

    private int INTR = 0;

    public void maskInterrupt(int interruptId) {
        INTR = INTR | (1 << interruptId);
    }

}
