package os;

import hardware.hdd.Disk;
import os.file_system.FileSystem;

public class OperatingSystem implements SystemCall {

    public final FileSystem fileSystem;

    public OperatingSystem(Disk disk) {
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
    public String open(String name) {
        return fileSystem.open(name);
    }
}
