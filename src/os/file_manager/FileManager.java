package os.file_manager;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.*;

public class FileManager {

    private static final String STORAGE_PATH = "Storage";
    private MiniOSDirectory currentDirectory = null;

    public FileManager() {
        currentDirectory = new MiniOSDirectory("root");
        currentDirectory.addFile(new MiniOSFile("SumOneToTen", 1, 12));
    }

    public List<Long> getValues(String name) {
        try {
            List<Long> values = new ArrayList<>();
            Scanner scanner = new Scanner(new File(STORAGE_PATH));
            MiniOSFile file = currentDirectory.getFile(name);
            if (file == null) return null;
            for (int idx = 0; idx < file.base; idx++) scanner.nextLine();
            for (int idx = 0; idx < file.limit; idx++) values.add(Long.valueOf(scanner.nextLine()));
            return values;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
