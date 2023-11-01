package main;

import interrupt.bus.Bus;
import interrupt.logger.MiniVMLogger;
import hardware.cpu.CPU;
import hardware.HWName;
import hardware.Memory;
import hardware.HIRQ;
import hardware.io_device.IODevice;
import hardware.io_device.Out;
import hardware.storage.Storage;
import os.OS;
import os.SystemCall;
import visualizer.MiniVMVisualizer;

import javax.swing.*;

public class MiniVM {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // create hardware
        Bus<HIRQ> controlBus = new Bus();
        CPU cpu = new CPU();
        IODevice memory = new Memory();
        IODevice storage = new Storage(4096);
        IODevice out0 = new Out();
        IODevice out1 = new Out();
        // associate control bus with hardware
        cpu.associate(controlBus, HWName.CPU);
        memory.associate(controlBus, HWName.MEMORY);
        storage.associate(controlBus, HWName.STORAGE);
        out0.associate(controlBus, "OUT0");
        out1.associate(controlBus, "OUT1");
        // create os and associate with hardware
        SystemCall os = new OS();
        os.associate(cpu);
        os.associate(memory);
        os.associate(storage);
        os.associate(out0);
        os.associate(out1);
        cpu.associate(os);
        // run hardware
        new Thread(memory).start();
        new Thread(storage).start();
        new Thread(out0).start();
        new Thread(out1).start();
        new Thread(cpu).start();
        // run visualizer
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        new MiniVMVisualizer(cpu, (Memory) memory, os).setVisible(true);
        MiniVMLogger.info("MiniVM", "MiniVM is running.");
    }

}
