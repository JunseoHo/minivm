package main;

import hardware.disk.Disk;
import os.OperatingSystem;
import os.shell.Shell;
import visualizer.MiniVMVisualizer;

public class MiniVM {

    public static void main(String[] args) {
        Disk disk = new Disk();
        OperatingSystem os = new OperatingSystem(disk);
        MiniVMVisualizer visualizer = new MiniVMVisualizer(os, new Shell(os));
        visualizer.setVisible(true);
    }

}
