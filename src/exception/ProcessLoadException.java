package exception;

public class ProcessLoadException extends Exception {

    public ProcessLoadException(int interruptId) {
        System.err.println("Process load failed : " + interruptId);
    }

}
