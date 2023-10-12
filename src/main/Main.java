package main;

import bios.BIOS;
import hardware.cpu.CPU;
import hardware.memory.Memory;
import os.MiniOS;
import os.SystemCall;

public class Main {

    public static void main(String[] args) {
        // create modules
        CPU cpu = new CPU();
        Memory memory = new Memory();
        BIOS bios = new BIOS();
        SystemCall operatingSystem = new MiniOS();
        // bind hardware
        cpu.bindHardware(memory);
        bios.bindHardware(cpu, memory);
        // install system
        cpu.installSystem(bios);
        bios.installSystem(operatingSystem);
        // run
        cpu.run();
    }

}
