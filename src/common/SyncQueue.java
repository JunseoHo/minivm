package common;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SyncQueue<T> {

    private Queue<T> queue = new LinkedList<>();
    private Semaphore sem = new Semaphore(1);

    public synchronized boolean add(T o) {
        try {
            sem.acquire();
            boolean result = queue.add(o);
            sem.release();
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized T poll() {
        try {
            sem.acquire();
            T o = queue.poll();
            sem.release();
            return o;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean isEmpty() {
        try {
            sem.acquire();
            boolean b = queue.isEmpty();
            sem.release();
            return b;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized int size() {
        try {
            sem.acquire();
            int size = queue.size();
            sem.release();
            return size;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Queue<T> getQueue() {
        try {
            sem.acquire();
            Queue<T> q = new LinkedList<>(queue);
            sem.release();
            return q;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
