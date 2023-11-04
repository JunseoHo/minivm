package hardware;

public interface IODevice {

    Byte read(int addr);

    Byte[] read(int addr, int size);

    boolean write(int addr, byte val);

    boolean write(int addr, byte[] val);

    int size();

}
