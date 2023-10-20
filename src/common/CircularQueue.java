package common;

import exception.QueueError;

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
        try {
            if (isFull()) throw new QueueError("Queue is full.");
            queue.set(rear++, o);
            if (rear == queueSize) rear = 0;
            ++currentSize;
            return true;
        } catch (QueueError e) {
            return false;
        }
    }

    public synchronized T dequeue() {
        try {
            if (isEmpty()) throw new QueueError("Queue is empty.");
            T o = queue.get(front++);
            if (front == queueSize) front = 0;
            --currentSize;
            return o;
        } catch (QueueError e) {
            return null;
        }
    }

    public int size() {
        return currentSize;
    }

    @Override
    public String toString() {
        String str = "";
        for (T o : queue) str += o + " ";
        return str;
    }

}
