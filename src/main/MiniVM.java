package main;

import hardware.disk.Disk;
import hardware.mmu.MMU;
import hardware.ram.RAM;
import os.OperatingSystem;
import os.shell.Shell;
import visualizer.MiniVMVisualizer;

public class MiniVM {

    public static void main(String[] args) {
        MMU mmu = new MMU();
        RAM ram = new RAM();
        Disk disk = new Disk();
        OperatingSystem os = new OperatingSystem(mmu, ram, disk);
        MiniVMVisualizer visualizer = new MiniVMVisualizer(os, new Shell(os));
        visualizer.setVisible(true);
    }

}
