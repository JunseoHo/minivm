package common;

import java.util.ArrayList;
import java.util.List;

public class CircularQueue<T> {

    private static final int MAX_SIZE = 8192, MIN_SIZE = 2, DEFAULT_SIZE = 1024;
    private int front = 0, rear = 0, currentSize = 0, queueSize = 0;
    private List<T> queue;

    public CircularQueue() {
        this(DEFAULT_SIZE);
    }

    public CircularQueue(int queueSize) {
        this.queueSize = queueSize < MIN_SIZE || queueSize > MAX_SIZE ? DEFAULT_SIZE : queueSize;
        queue = new ArrayList<>(this.queueSize);
        for (int index = 0; index < this.queueSize; index++) queue.add(null);
    }

    public synchronized boolean isEmpty() {
        return currentSize == 0;
    }

    public synchronized boolean isFull() {
        return currentSize == queueSize;
    }

    public synchronized boolean enqueue(T o) {
        if (isFull()) {
            System.err.println("queue is full.");
            return false;
        }
        queue.set(rear++, o);
        if (rear == queueSize) rear = 0;
        ++currentSize;
        return true;
    }

    public synchronized T dequeue() {
        if (isEmpty()) {
            System.err.println("queue is empty.");
            return null;
        }
        T o = queue.get(front++);
        if (front == queueSize) front = 0;
        --currentSize;
        return o;
    }

    public synchronized int size() {
        return currentSize;
    }

}
