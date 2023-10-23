package common;

import java.util.ArrayList;
import java.util.List;

public class CircularQueue<T> {
    // constants
    private static final int MAX_SIZE = 8192;
    private static final int MIN_SIZE = 2;
    private static final int DEFAULT_SIZE = 1024;
    // working variables
    private int front = 0;
    private int rear = 0;
    private int currentSize = 0;
    private int queueSize = 0;
    // container
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
        if (isFull()) return false;
        queue.set(rear++, o);
        if (rear == queueSize) rear = 0;
        ++currentSize;
        return true;
    }

    public synchronized T dequeue() {
        if (isEmpty()) return null;
        T o = queue.get(front++);
        if (front == queueSize) front = 0;
        --currentSize;
        return o;
    }

    public int size() {
        return currentSize;
    }

    public int capacity() {
        return queueSize;
    }

    @Override
    public String toString() {
        String str = "[ ";
        if (front < rear) for (int index = front; index < rear; index++) str += queue.get(index) + " ";
        else if (front > rear) {
            for (int index = front; index < queueSize; index++) str += queue.get(index) + " ";
            for (int index = 0; index < rear; index++) str += queue.get(index) + " ";
        } else str = "empty";
        return str + " ]";
    }

}
