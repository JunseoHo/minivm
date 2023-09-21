package os.file_manager;

import java.util.ArrayList;
import java.util.List;

public class MiniOSDirectory {

    public MiniOSDirectory parentDirectory;
    public String name;
    public List<MiniOSDirectory> directories;
    public List<MiniOSFile> files;

    public MiniOSDirectory(String name) {
        parentDirectory = null;
        this.name = name;
        directories = new ArrayList<>();
        files = new ArrayList<>();
    }

    public void addFile(MiniOSFile file) {
        files.add(file);
    }

    public MiniOSFile getFile(String name) {
        for (MiniOSFile file : files) if (file.name.equals(name)) return file;
        return null;
    }

}
