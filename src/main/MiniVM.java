package main;

import common.MiniVMUtils;
import hardware.cpu.CPU;
import hardware.disk.Disk;
import hardware.ram.RAM;
import os.OperatingSystem;
import os.compiler.Compiler;
import os.shell.Shell;
import visualizer.MiniVMVisualizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class MiniVM {

    public static void main(String[] args) throws FileNotFoundException {
        CPU cpu = new CPU();
        RAM ram = new RAM();
        Disk disk = new Disk();
        OperatingSystem os = new OperatingSystem(cpu, ram, disk);
        cpu.associate(os);
        new Thread(cpu).start();
        new MiniVMVisualizer(os, new Shell(os)).setVisible(true);
    }

    /*
        head = 0
        0 -> 8
        1 -> 0
        2 -> 0
        3 -> 0
        4 -> 0
        5 -> 0
        6 -> 0
        7 -> 0

        allocate(3)
        head = 3
        0 -> 8
        1 -> 0
        2 -> 0
        3 -> 5
        4 -> 0
        5 -> 0
        6 -> 0
        7 -> 0
        (0, 3)

        allocate(4)
        head = 7
        0 -> ?
        1 -> ?
        2 -> ?
        3 -> ?
        4 -> ?
        5 -> ?
        6 -> ?
        7 -> 1
        (0, 3)
        (3, 4)

        allocate(1)
        head = 3
        0 -> 2
        1 -> 0
        2 -> ?
        3 -> 4
        4 -> 0
        5 -> 0
        6 -> 0
        7 -> ?
        (7, 1)
     */

}
