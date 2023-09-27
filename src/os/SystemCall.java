package os;

public interface SystemCall {
    String ls();

    void readStdin(int base, int limit);

    void writeStdout(int base, int limit);

    void releaseRunningProcess();

    void eventWaitRunningProcess();

    void contextSwitch();
}
