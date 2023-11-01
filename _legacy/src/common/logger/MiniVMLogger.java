package common.logger;

public class MiniVMLogger {

    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String GREEN = "\u001B[32m";
    public static final String END = "\u001B[0m";

    public static void info(String host, String message) {
        System.out.printf(GREEN + "INFO  " + END + "|%-16s : " + message + "\n", host);
    }

    public static void warn(String host, String message) {
        System.out.printf(YELLOW + "WARN  " + END + "|%-16s : " + message + "\n", host);
    }

    public static void error(String host, String message) {
        System.out.printf(RED + "ERROR " + END + "|%-16s : " + message + "\n", host);
    }

}
