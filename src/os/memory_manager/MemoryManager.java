package os.memory_manager;

import os.OSModule;

public class MemoryManager extends OSModule {

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    @Override
    public void handleInterrupt() {

    }
}
