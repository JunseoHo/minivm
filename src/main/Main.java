package main;

import common.Bus;
import hardware.CPU;
import hardware.Memory;
import hardware.interrupt.IOInterrupt;
import hardware.io_device.IODevice;
import hardware.io_device.InputDevice;
import hardware.io_device.OutputDevice;
import hardware.storage.Storage;

public class Main {

    public static void main(String[] args) {
        // create hardware
        Bus<IOInterrupt> controlBus = new Bus();
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
        // run hardware
        new Thread(memory).start();
        new Thread(storage).start();
        new Thread(inputDevice).start();
        new Thread(outputDevice).start();
        new Thread(cpu).start();
    }

}
