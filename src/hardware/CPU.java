package hardware;

public class CPU implements Runnable {

    private int INTR = 0;

    public void maskInterrupt(int interruptId) {
        INTR = INTR | (1 << interruptId);
    }

    @Override
    public void run() {

    }
}
