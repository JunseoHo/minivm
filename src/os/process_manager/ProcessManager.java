package os.process_manager;

import common.CircularQueue;
import hardware.CPU;
import hardware.Memory;
import hardware.io_device.IODevice;
import os.OSModule;
import os.file_manager.File;

public class ProcessManager extends OSModule {
    // hardware
    private CPU cpu;
    private IODevice memory;
    // components
    private Scheduler scheduler = new Scheduler();
    private Loader loader = new Loader();

    @Override
    public void associate(CPU cpu) {
        this.cpu = cpu;
    }

    @Override
    public void associate(IODevice memory) {
        this.memory = memory;
    }

    @Override
    public void handleInterrupt() {

    }

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    private class Scheduler {
        private Process runningProcess;
        private CircularQueue<Process> readyQueue = new CircularQueue<>();
        private CircularQueue<Process> blockQueue = new CircularQueue<>();

        public void switchContext() {

        }

    }

    private class Loader {

        public void load(File file) {
            
        }

    }

}
