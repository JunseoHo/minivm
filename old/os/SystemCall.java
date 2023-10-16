package operating_system;

import hardware.cpu.CPU;
import hardware.memory.Memory;

public abstract class SystemCall{
    // Attributes
    private String name;

    public SystemCall(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void associate(CPU cpu, Memory memory);
    public abstract void init();

    public abstract void run();

    public abstract void exitRunningProcess();
}
