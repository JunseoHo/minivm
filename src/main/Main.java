package main;

import hardware.CPU;
import hardware.ControlBus;
import hardware.Memory;
import hardware.storage.Storage;

public class Main {

    public static void main(String[] args) {
        CPU cpu = new CPU();
        Memory memory = new Memory();
        Storage storage = new Storage();
        ControlBus controlBus = new ControlBus();
    }

}
