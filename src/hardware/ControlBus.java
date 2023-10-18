package hardware;

import common.CircularQueue;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class ControlBus implements Runnable {
    // attributes
    private CircularQueue<Integer> bus;
    // associations
    private CPU cpu;

    public ControlBus(){
        bus = new CircularQueue<>();
    }

    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    public boolean send(Integer data) {
        return bus.enqueue(data);
    }

    @Override
    public void run() {
        if (cpu == null)
            System.err.println("control bus is not associated with the CPU.");
        else while (true) {
            while (!bus.isEmpty()) cpu.maskInterrupt(bus.dequeue());
        }
    }
}
