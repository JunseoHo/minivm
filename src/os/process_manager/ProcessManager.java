package os.process_manager;

import common.CircularQueue;
import hardware.cpu.CPU;
import hardware.io_device.IODevice;
import os.OSModule;
import os.SIQ;
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
    private CircularQueue<Integer> processIdQueue = new CircularQueue<>();

    public ProcessManager() {
        for (int processId = 0; processId < processIdQueue.size(); processId++)
            processIdQueue.enqueue(processId);
    }

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

    public void newProcess(String fileName) {
        loader.newProcess(fileName);
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

        public void newProcess(String fileName) {
            send(new SIQ(SWName.FILE_MANAGER, SIQ.REQUEST_FILE, fileName));
            SIQ intr = receive(SIQ.RESPONSE_FILE, SIQ.FILE_NOT_FOUND);
            if (intr.id == SIQ.FILE_NOT_FOUND) {
                System.err.println("file not found.");
                return;
            }
            File file = (File) intr.values[0];
            if (file.type != FileType.EXECUTABLE) {
                System.err.println("file type is not executable.");
                return;
            }
            send(new SIQ(SWName.MEMORY_MANAGER, SIQ.REQUEST_PAGES, 2));
            intr = receive(SIQ.RESPONSE_PAGES, SIQ.OUT_OF_MEMORY);
            if (intr.id == SIQ.OUT_OF_MEMORY) {
                System.err.println("out of memory.");
                return;
            }
            Process process = new Process(processIdQueue.dequeue());
            List<Page> pages = (List<Page>) intr.values[0];
            process.setPage(pages.get(0), pages.get(1));
            send(new SIQ(SWName.MEMORY_MANAGER, SIQ.REQUEST_MEMORY_WRITE, pages.get(0).base, file.getRecords()));
            receive(SIQ.RESPONSE_MEMORY_WRITE);
            scheduler.admit(process);
        }
    }

}