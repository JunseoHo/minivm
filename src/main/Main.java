package main;

import hardware.CPU;
import hardware.Memory;
import os.OperatingSystem;
import view.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // config
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        // init modules
        CPU cpu = new CPU();
        Memory memory = new Memory();
        // associate modules
        cpu.associate(memory);
        // run
        new Thread(cpu).start();
    }

}
