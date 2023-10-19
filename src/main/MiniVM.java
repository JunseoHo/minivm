package main;

import common.bus.Bus;
import hardware.cpu.CPU;
import hardware.HWName;
import hardware.Memory;
import hardware.HIQ;
import hardware.io_device.IODevice;
import hardware.io_device.InputDevice;
import hardware.io_device.OutputDevice;
import hardware.storage.Storage;
import os.OS;
import os.SystemCall;

public class MiniVM {

    public static void main(String[] args) {
        // create hardware
        Bus<HIQ> controlBus = new Bus();
        CPU cpu = new CPU();
        IODevice memory = new Memory();
        IODevice storage = new Storage();
        IODevice inputDevice = new InputDevice();
        IODevice outputDevice = new OutputDevice();
        // associate control bus with hardware
        cpu.associate(controlBus, HWName.CPU);
        memory.associate(controlBus, HWName.MEMORY);
        storage.associate(controlBus, HWName.STORAGE);
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
