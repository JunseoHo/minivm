package main;

import hardware.CPU;
import hardware.ControlBus;
import hardware.Memory;
import hardware.io_device.IODevice;
import hardware.io_device.InputDevice;
import hardware.io_device.OutputDevice;
import hardware.storage.Storage;

public class Main {

    public static void main(String[] args) {
        // create hardware
        CPU cpu = new CPU();
        IODevice memory = new Memory();
        IODevice storage = new Storage();
        IODevice inputDevice = new InputDevice();
        IODevice outputDevice = new OutputDevice();
        ControlBus controlBus = new ControlBus();
        // associate control bus
        controlBus.associate(cpu);
        memory.associate(controlBus);
        storage.associate(controlBus);
        inputDevice.associate(controlBus);
        outputDevice.associate(controlBus);
    }

}
