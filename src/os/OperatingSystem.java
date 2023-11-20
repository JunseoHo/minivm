package os;

import hardware.disk.Disk;
import hardware.ram.RAM;
import os.file_system.FileSystem;
import os.memory_manager.MemoryManager;

public class OperatingSystem implements SystemCall {

    public final MemoryManager memoryManager;
    public final FileSystem fileSystem;

    public OperatingSystem(RAM ram, Disk disk) {
        this.memoryManager = new MemoryManager(ram);
        this.fileSystem = new FileSystem(disk);
    }

    @Override
    public String mkdir(String name) {
        return fileSystem.mkdir(name);
    }

    @Override
    public String rmdir(String name) {
        return fileSystem.rmdir(name);
    }

    @Override
    public String ls() {
        return fileSystem.ls();
    }

    @Override
    public String touch(String name) {
        return fileSystem.touch(name);
    }

    @Override
    public String rm(String name) {
        return fileSystem.rm(name);
    }

    @Override
    public String cd(String name) {
        return fileSystem.cd(name);
    }

    @Override
    public String save() {
        return fileSystem.saveDiskImage();
    }

}
