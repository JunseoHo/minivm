package main;

import hardware.CPU;
import hardware.Memory;
import os.OperatingSystem;
import view.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        MainFrame mainFrame = new MainFrame();
        OperatingSystem os = new OperatingSystem();
        CPU cpu = new CPU();
        Memory memory = new Memory();
        mainFrame.associate(os, cpu, memory);
        mainFrame.setVisible(true);
    }

}
