package common.bus;

import common.CircularQueue;
import exception.BusError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus<T extends Event> {

    private final Map<String, CircularQueue<T>> components;

    public Bus() {
        components = new HashMap<>();
    }

    public synchronized boolean register(String name) {
        try {
            if (components.containsKey(name)) throw new BusError(name + " has already been registered.");
            return components.put(name, new CircularQueue<>()) != null;
        } catch (BusError e) {
            return false;
        }
    }

    public synchronized boolean unregister(String name) {
        try {
            if (!components.containsKey(name)) throw new BusError(name + " is not registered.");
            return components.remove(name) != null;
        } catch (BusError e) {
            return false;
        }
    }

    public synchronized boolean send(T o) {
        try {
            CircularQueue<T> queue = components.get(o.receiver);
            if (queue == null) throw new BusError("Unknown receiver.");
            if (queue.isFull()) throw new BusError("Event queue is full.");
            return queue.enqueue(o);
        } catch (BusError e) {
            return false;
        }
    }

    public synchronized T receive(String name) {
        try {
            CircularQueue<T> queue = components.get(name);
            if (queue == null) throw new BusError("Unknown receiver.");
            if (queue.isEmpty()) throw new BusError();
            return queue.dequeue();
        } catch (BusError e) {
            return null;
        }
    }

    public synchronized List<T> receiveAll(String name) {
        try {
            List<T> events = new ArrayList<>();
            CircularQueue<T> queue = components.get(name);
            if (queue == null) throw new BusError("Unknown receiver.");
            if (queue.isEmpty()) return events;
            while (!queue.isEmpty()) events.add(queue.dequeue());
            return events;
        } catch (BusError e) {
            return null;
        }
    }

}
