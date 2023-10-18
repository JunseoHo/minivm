package common;

import javax.sound.midi.Receiver;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus<T extends Event> {

    private Map<String, CircularQueue<T>> components;

    public Bus() {
        components = new HashMap<>();
    }

    public synchronized boolean register(String name) {
        if (components.containsKey(name)) return false;
        components.put(name, new CircularQueue<>());
        return true;
    }

    public synchronized boolean unregister(String name) {
        if (!components.containsKey(name)) return false;
        components.remove(name);
        return true;
    }

    public synchronized boolean send(T o) {
        CircularQueue<T> queue = components.get(o.receiver);
        if (queue == null || queue.isFull()) return false;
        return queue.enqueue(o);
    }

    public T receive(String name) {
        CircularQueue<T> queue = components.get(name);
        if (queue == null || queue.isEmpty()) return null;
        return queue.dequeue();
    }

    public List<T> receiveAll(String name) {
        List<T> events = new ArrayList<>();
        CircularQueue<T> queue = components.get(name);
        while (!queue.isEmpty()) events.add(queue.dequeue());
        return events;
    }
}
