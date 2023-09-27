package os;

import os.file_manager.FileManager;

public class OperatingSystem implements SystemCall {

    private FileManager fileManager = null;

    public OperatingSystem() {
        fileManager = new FileManager();
    }

    @Override
    public String ls() {
        return fileManager.ls();
    }

}
