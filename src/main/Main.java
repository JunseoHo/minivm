package main;

import common.Bus;
import hardware.CPU;
import hardware.Memory;
import hardware.HWInterrupt;
import hardware.io_device.IODevice;
import hardware.io_device.InputDevice;
import hardware.io_device.OutputDevice;
import hardware.storage.Storage;
import os.OS;
import os.SystemCall;
import os.file_manager.File;
import os.file_manager.FileType;

public class Main {

    public static void main(String[] args) {
        // create hardware
        Bus<HWInterrupt> controlBus = new Bus();
        CPU cpu = new CPU();
        IODevice memory = new Memory();
        IODevice storage = new Storage();
        IODevice inputDevice = new InputDevice();
        IODevice outputDevice = new OutputDevice();
        // associate control bus with hardware
        cpu.associate(controlBus, "CPU");
        memory.associate(controlBus, "Memory");
        storage.associate(controlBus, "Storage");
        inputDevice.associate(controlBus, "InputDevice");
        outputDevice.associate(controlBus, "OutputDevice");
        // create os and associate with hardware
        SystemCall os = new OS();
        os.associate(cpu);
        os.associate(memory);
        os.associate(storage);
        os.associate(inputDevice);
        os.associate(outputDevice);
        cpu.associate(os);
        // run hardware
        new Thread(memory).start();
        new Thread(storage).start();
        new Thread(inputDevice).start();
        new Thread(outputDevice).start();
        new Thread(cpu).start();
    }

}
