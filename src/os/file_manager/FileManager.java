package os.file_manager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileManager {
    // Attributes
    private static final String FILE_INDEX_PATH = "file_index.json";
    private static final String STORAGE_PATH = "storage";
    private MiniOSFile currentDir = null;

    public FileManager() {
        try {
            currentDir = init((JSONObject) new JSONParser().parse(new FileReader(FILE_INDEX_PATH)));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MiniOSFile init(JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        String type = (String) jsonObject.get("type");
        MiniOSFile file = new MiniOSFile(name, type);
        if (type.equals("directory")) {
            JSONArray children = (JSONArray) jsonObject.get("children");
            for (int idx = 0; idx < children.size(); idx++) file.addFile(init((JSONObject) children.get(idx)));
        } else if (type.equals("file")) {
            file.location = (long) jsonObject.get("location");
            file.size = (long) jsonObject.get("size");
        }
        return file;
    }

    public List<Long> getValues(String name) {
//        try {
//            List<Long> values = new ArrayList<>();
//            Scanner scanner = new Scanner(new File(STORAGE_PATH));
//            MiniOSFile file = currentDir.getFile(name);
//            if (file == null) return null;
//            for (int idx = 0; idx < file.base; idx++) scanner.nextLine();
//            for (int idx = 0; idx < file.limit; idx++) values.add(Long.valueOf(scanner.nextLine()));
//            return values;
//        } catch (FileNotFoundException e) {
//            return null;
//        }
        return null;
    }

    public void ls() {
        System.out.println("Directory Name : " + currentDir.name);
        System.out.printf("%-12s%-20s%-5s\n", "Type", "Name", "Size");
        System.out.printf("-------------------------------------\n");
        for (MiniOSFile child : currentDir.children) child.print();
    }
}
