package os;

import hardware.cpu.CPU;
import hardware.cpu.Context;
import hardware.memory.Memory;
import main.MiniOSUtil;
import os.file_manager.FileManager;
import os.memory_manager.MemoryManager;
import os.memory_manager.Page;
import os.process_manager.Process;
import os.process_manager.ProcessManager;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class MiniOS extends SystemCall {
    // Associations
    private CPU cpu = null;
    private Memory memory = null;
    // Modules
    private ProcessManager processManager = null;
    private MemoryManager memoryManager = null;
    private FileManager fileManager = null;

    public MiniOS(String name) {
        super(name);
    }

    public void associate(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    public void init() {
        processManager = new ProcessManager(cpu);
        memoryManager = new MemoryManager(memory);
        fileManager = new FileManager();
    }

    private void execute(String exeName) {
        List<Long> program = fileManager.getProgram(exeName);
        for (long l : program) System.out.println(l);
        Page codeSegment = memoryManager.getPage();
        Page dataSegment = memoryManager.getPage();
        Process process = new Process(new Context(0, 0, 0, 0, 0, 0, 0, 0, codeSegment.base,
                dataSegment.base, true, false, false, false, false), codeSegment, dataSegment);
        memoryManager.load(codeSegment, program);
        processManager.load(process);
    }

    public void run() {
        processManager.start();
        new Thread(memoryManager).start();
        System.out.println("Welcome to " + getName() + ".");
        while (true) {
            System.out.print("$> ");
            String input[] = (new Scanner(System.in).nextLine().split(" "));
            switch (input[0]) {
                case "ls" -> fileManager.ls();
                case "cd" -> fileManager.cd(input[1]);
                case "execute" -> execute(input[1]);
                case "exit" -> {
                    System.out.println("Thank you for using " + getName() + ".");
                    return;
                }
                default -> System.out.println("Command not found.");
            }
            while (cpu.isRunning()) MiniOSUtil.sleep(100);
        }
    }

    @Override
    public void exitRunningProcess() {
        processManager.exit();
    }
}
