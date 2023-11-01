package os.file_manager;

import java.util.ArrayList;
import java.util.List;

public class File {

    public FileType type;
    public String name;
    public long base = 0;
    public long size = 0;
    public File parent = null;
    public List<Long> records = new ArrayList<>();
    public List<File> children = new ArrayList<>();

    public File(FileType type) {
        this.type = type;
    }

    public void addRecord(long record) {
        records.add(record);
    }

    public void addChild(File file) {
        children.add(file);
    }

    public List<Long> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        String str = "[ " + type + " : " + name + " ]\n";
        for (File file : children) str += file.type + " : " + file.name + "\n";
        return str;
    }

}
