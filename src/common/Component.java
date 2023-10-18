package common;

import hardware.CPU;

import java.util.List;

public class Component<T extends Event> {

    private Bus<T> bus;
    private String name;

    public boolean associate(Bus<T> bus, String name) {
        this.bus = bus;
        this.name = name;
        return bus.register(name);
    }

    protected boolean send(T o) {
        if (bus == null) return false;
        return bus.send(o);
    }

    protected T receive() {
        T o;
        while ((o = tryReceive()) == null) ;
        return o;
    }

    protected List<T> receiveAll() {
        return bus.receiveAll(name);
    }

    protected T tryReceive() {
        return bus.receive(name);
    }

}
