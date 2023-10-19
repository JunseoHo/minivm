package exception;

public class BusError extends Exception {

    public BusError() {
        /* DO NOTHING */
    }

    public BusError(String message) {
        System.err.println("bus error : " + message);
    }

}
