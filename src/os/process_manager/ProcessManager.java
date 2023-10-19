package os.process_manager;

import common.CircularQueue;
import hardware.cpu.CPU;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SWInterrupt;
import os.SWName;
import os.file_manager.File;
import os.file_manager.FileType;
import os.memory_manager.Page;

import java.util.List;

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

    public void newProcess(File file) {
        loader.load(file);
    }

    @Override
    public void run() {
        while (true) {
            handleInterrupt();
        }
    }

    public void switchContext() {
        scheduler.switchContext();
    }

    private class Scheduler {
        private Process runningProcess;
        private CircularQueue<Process> readyQueue = new CircularQueue<>();
        private CircularQueue<Process> blockQueue = new CircularQueue<>();

        public void admit(Process process) {
            if (runningProcess == null) {
                runningProcess = process;
                cpu.restore(process.save());
            } else readyQueue.enqueue(process);
        }

        public void terminate() {

        }

        public void switchContext() {
            if (readyQueue.isEmpty()) return;
            runningProcess.restore(cpu.save());
            readyQueue.enqueue(runningProcess);
            runningProcess = readyQueue.dequeue();
            cpu.restore(runningProcess.save());
        }

    }

    private class Loader {

        public void load(File file) {
            if (file.type != FileType.EXECUTABLE) {
                send(new SWInterrupt(SWName.PROCESS_MANAGER, 0x02));
                return;
            }
            send(new SWInterrupt(SWName.MEMORY_MANAGER, 0x32, 2));
            interrupt = receive(0x33, 0x34);
            if (interrupt.id == 0x33) {
                Process process = new Process(0);
                List<Page> pages = (List<Page>) interrupt.values[0];
                Page codeSegment = pages.get(0);
                Page dataSegment = pages.get(1);
                process.setPage(codeSegment, dataSegment);
                send(new SWInterrupt(SWName.MEMORY_MANAGER, 0x35, codeSegment.base, file.getRecords()));
                receive(0x36);
                scheduler.admit(process);
            }
        }

    }

}
