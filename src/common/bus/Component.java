package common.bus;

import common.CircularQueue;
import common.bus.Bus;
import common.bus.Event;
import exception.BusError;

import java.util.List;

public class Component<T extends Event> {

    private Bus<T> bus;
    private String name;
    protected CircularQueue<T> queue = new CircularQueue<>();

    public boolean associate(Bus<T> bus, String name) {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            if (name == null) throw new BusError("Component name is null.");
            if (this.bus != null) throw new BusError("Bus has already been associated.");
            if (this.name != null) throw new BusError("Component name has already been defined.");
            this.bus = bus;
            this.name = name;
            return bus.register(name);
        } catch (BusError e) {
            return false;
        }
    }

    protected boolean send(T o) {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            return bus.send(o);
        } catch (BusError e) {
            return false;
        }
    }

    protected T tryReceive() {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            if (name == null) throw new BusError("Component name is null.");
            return bus.receive(name);
        } catch (BusError e) {
            return null;
        }
    }

    protected T receive() {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            if (name == null) throw new BusError("Component name is null.");
            T o;
            while ((o = tryReceive()) == null) ;
            return o;
        } catch (BusError e) {
            return null;
        }
    }

    protected T receive(int... targetIds) {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            if (name == null) throw new BusError("Component name is null.");
            while (true) {
                T o = receive();
                for (int id : targetIds) {
                    if (o.id == id) return o;
                }
                queue.enqueue(o);
            }
        } catch (BusError e) {
            return null;
        }
    }

    protected List<T> receiveAll() {
        try {
            if (bus == null) throw new BusError("Bus is null.");
            if (name == null) throw new BusError("Component name is null.");
            return bus.receiveAll(name);
        } catch (BusError e) {
            return null;
        }
    }

}
