package common;

import java.util.LinkedList;
import java.util.Queue;

public class SyncQueue<T> {

    private Queue<T> queue = new LinkedList<>();

    public synchronized boolean enqueue(T o) {
        return queue.add(o);
    }

    public synchronized T dequeue() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

}
