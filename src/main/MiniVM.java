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
//        String fileName = "assembly_source/sum5";
//        Scanner scanner = new Scanner(new File(fileName));
//        String program = "";
//        while (scanner.hasNextLine()) program += scanner.nextLine() + "\n";
//        List<Integer> machineCodes = Compiler.compile(program);
//        System.out.println(os.createProcess(machineCodes));
    }

}
