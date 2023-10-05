package main;

import bios.BIOS;
import hardware.cpu.CPU;
import hardware.memory.Memory;
import os.MiniOS;

public class Main {

    public static void main(String[] args) throws Exception {
        CPU cpu = new CPU();
        Memory memory = new Memory();
        BIOS bios = new BIOS();
        bios.associate(cpu, memory);
        bios.install(new MiniOS("MiniOS"));
        cpu.associate(memory, bios);
        cpu.run();
    }

}
