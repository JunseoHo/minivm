package main;

import common.bus.Bus;
import hardware.cpu.CPU;
import hardware.HWName;
import hardware.Memory;
import hardware.HIQ;
import hardware.io_device.IODevice;
import hardware.io_device.StandardInput;
import hardware.io_device.StandardOutput;
import hardware.storage.Storage;
import monitor.HardwareMonitor;
import os.OS;
import os.SystemCall;
import shell.Shell;

public class MiniVM {

    public static void main(String[] args) {
        // create hardware
        Bus<HIQ> controlBus = new Bus();
        CPU cpu = new CPU();
        IODevice memory = new Memory();
        IODevice storage = new Storage();
        IODevice stdin = new StandardInput();
        IODevice stdout = new StandardOutput();
        // associate control bus with hardware
        cpu.associate(controlBus, HWName.CPU);
        memory.associate(controlBus, HWName.MEMORY);
        storage.associate(controlBus, HWName.STORAGE);
        stdin.associate(controlBus, HWName.STDIN);
        stdout.associate(controlBus, HWName.STDOUT);
        // create os and associate with hardware
        SystemCall os = new OS();
        os.associate(cpu);
        os.associate(memory);
        os.associate(storage);
        os.associate(stdin);
        os.associate(stdout);
        cpu.associate(os);
        // run hardware
        new Thread(memory).start();
        new Thread(storage).start();
        new Thread(stdin).start();
        new Thread(stdout).start();
        new Thread(cpu).start();
        // run shell
        new Thread(new Shell(os)).start();
    }

}
