package main;

import hardware.cpu.CPU;
import hardware.cpu.IA32;
import os.OperatingSystem;
import os.SystemCall;
import visualizer.MiniVMVisualizer;

public class MiniVM {

    public static void main(String[] args) {
        SystemCall os = new OperatingSystem();
        MiniVMVisualizer visualizer = new MiniVMVisualizer(os, new IA32());
        visualizer.setVisible(true);
    }

}
