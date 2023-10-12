package bios;

import hardware.cpu.CPU;
import hardware.memory.Memory;
import os.SystemCall;

public class BIOS implements Runnable {
    // Computing resources
    private CPU cpu = null;
    private Memory memory = null;
    // Attributes
    private SystemCall installedOS = null;

    public void bindHardware(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public void installSystem(SystemCall os) {
        installedOS = os;
    }

    public boolean powerOnSelfTest() {
        if (memory == null) {
            System.out.println("Check Memory mounting... Failed.");
            return false;
        } else System.out.println("Check Memory mounting... Done.");
        return true;
    }

    public void bootOS() {
        if (installedOS == null) {
            System.out.println("Installed operating system is not found.");
            cpu.interrupt("PowerOff");
            return;
        }
        System.out.println("Booting " + installedOS.getName() + "...");
        installedOS.associate(cpu, memory);
        installedOS.init();
        installedOS.run();
    }

    @Override
    public void run() {
        System.out.println("Run BIOS.");
        if (!powerOnSelfTest()) {
            System.out.println("System has problems.");
            cpu.interrupt("PowerOff");
            return;
        } else System.out.println("No system problems.");
        bootOS();
        System.out.println("Stop BIOS.");
        cpu.interrupt("PowerOff");
    }


}
