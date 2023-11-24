package main;

import hardware.cpu.CPU;
import hardware.disk.Disk;
import hardware.ram.RAM;
import os.OperatingSystem;
import visualizer.MiniVMVisualizer;

public class MiniVM {

    public static void main(String[] args) {
        CPU cpu = new CPU();
        RAM ram = new RAM();
        Disk disk = new Disk();
        OperatingSystem os = new OperatingSystem(cpu, ram, disk);
        cpu.associate(os);
        new Thread(cpu).start();
        new MiniVMVisualizer(os, null).setVisible(true);
    }

}
