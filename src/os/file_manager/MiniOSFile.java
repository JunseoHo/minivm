package os.file_manager;

import java.util.ArrayList;
import java.util.List;

public class MiniOSFile {

    public String name;
    public String type;
    public long location;
    public long size;
    public List<MiniOSFile> children;
    public MiniOSFile parent;

    public MiniOSFile(String name, String type) {
        this.name = name;
        this.type = type;
        location = 0;
        size = 0;
        this.children = new ArrayList<>();
        parent = null;
    }

    public void addFile(MiniOSFile file) {
        children.add(file);
    }

    public void print() {
        if (type.equals("directory")) System.out.printf("%-12s%-20s\n", "DIRECTORY", name);
        else if (type.equals("file")) System.out.printf("%-12s%-20s%-5d\n", "FILE", name, size);
    }

}
