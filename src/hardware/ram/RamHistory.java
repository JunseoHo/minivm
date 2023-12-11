package hardware.ram;

public class RamHistory {

    public String method;
    public int address;
    public int value;

    public RamHistory(String method, int address, int value) {
        this.method = method;
        this.address = address;
        this.value = value;
    }

}
