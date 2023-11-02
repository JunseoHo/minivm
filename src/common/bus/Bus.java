package common.bus;

import common.SyncQueue;
import interrupt.IRQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus<T extends IRQ> {

    private final Map<String, SyncQueue<T>> componentMap;

    public Bus() {
        componentMap = new HashMap<>();
    }

    public synchronized boolean register(String name) {
        if (componentMap.containsKey(name)) return false;
        return componentMap.put(name, new SyncQueue<>()) != null;
    }

    public synchronized boolean send(T o) {
        if (o == null || o.receiver == null) return false;
        SyncQueue<T> queue = componentMap.get(o.receiver);
        if (queue == null) return false;
        return queue.enqueue(o);
    }

    public synchronized T receive(String name) {
        if (name == null) return null;
        SyncQueue<T> queue = componentMap.get(name);
        if (queue == null || queue.isEmpty()) return null;
        return queue.dequeue();
    }

    public synchronized List<T> receiveAll(String name) {
        if (name == null) return null;
        SyncQueue<T> queue = componentMap.get(name);
        if (queue == null) return null;
        List<T> events = new ArrayList<>();
        if (queue.isEmpty()) return events;
        while (!queue.isEmpty()) events.add(queue.dequeue());
        return events;
    }

}
