package exception;

public class QueueError extends Exception {

    public QueueError() {
        /* DO NOTHING */
    }

    public QueueError(String message) {
        System.err.println("queue error : " + message);
    }

}
