package os.file_manager;

import java.util.ArrayList;
import java.util.List;

public class File {

    private FileType fileType;
    private List<Long> records = new ArrayList<>();

    public File(FileType fileType) {
        this.fileType = fileType;
    }

    public FileType getType() {
        return fileType;
    }

    public void addRecord(long record) {
        records.add(record);
    }

    public List<Long> getRecords() {
        return records;
    }

}
