package common.bus;

import common.CircularQueue;
import common.bus.Bus;
import common.bus.Event;

import java.util.List;

public class Component<T extends Event> {

    private Bus<T> bus;
    private String name;
    protected CircularQueue<T> queue;

    public Component() {
        bus = null;
        name = null;
        queue = new CircularQueue<>();
    }

    public boolean associate(Bus<T> bus, String name) {
        if (bus == null || name == null || this.bus != null || this.name != null) return false;
        this.bus = bus;
        this.name = name;
        return bus.register(name);
    }

    public String name() {
        return name;
    }

    protected boolean send(T o) {
        if (bus == null) return false;
        return bus.send(o);
    }

    protected T tryReceive() {
        if (bus == null || name == null) return null;
        return bus.receive(name);
    }

    protected T receive() {
        if (bus == null || name == null) return null;
        T o;
        while ((o = tryReceive()) == null) ;
        return o;
    }

    protected T receive(int... targetIds) {
        if (bus == null || name == null) return null;
        while (true) {
            T o = receive();
            for (int id : targetIds) if (o.id() == id) return o;
            queue.enqueue(o);
        }
    }

    protected List<T> receiveAll() {
        if (bus == null || name == null) return null;
        return bus.receiveAll(name);
    }

    protected boolean enqueue(T o) {
        if (o == null) return false;
        return queue.enqueue(o);
    }

    protected T dequeue() {
        if (queue.isEmpty()) return null;
        return queue.dequeue();
    }

    protected boolean isEmpty() {
        return queue.isEmpty();
    }

}
