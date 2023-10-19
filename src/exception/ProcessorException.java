package exception;

public class ProcessorException extends Exception {

    public ProcessorException(String message) {
        System.err.println("cpu error : " + message);
    }

}
