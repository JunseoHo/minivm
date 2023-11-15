package os;

import hardware.disk.Disk;
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
    public String rm(String name) { return fileSystem.rm(name); }

    @Override
    public String cd(String name) {
        return fileSystem.cd(name);
    }

    @Override
    public String save() {
        return fileSystem.saveDiskImage();
    }

}
