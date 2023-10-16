package operating_system.file_manager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileManager {
    // Attributes
    private static final String FILE_INDEX_PATH = "file_index.json";
    private static final String STORAGE_PATH = "disk_image";
    private MiniOSFile currentDir = null;

    public FileManager() {
        try {
            currentDir = init(null, (JSONObject) new JSONParser().parse(new FileReader(FILE_INDEX_PATH)));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public MiniOSFile init(MiniOSFile parent, JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        String type = (String) jsonObject.get("type");
        MiniOSFile file = new MiniOSFile(name, type);
        if (type.equals("directory")) {
            JSONArray children = (JSONArray) jsonObject.get("children");
            for (int idx = 0; idx < children.size(); idx++) file.addFile(init(file, (JSONObject) children.get(idx)));
        } else if (type.equals("file")) {
            file.location = (long) jsonObject.get("location");
            file.size = (long) jsonObject.get("size");
        }
        file.parent = parent;
        return file;
    }

    public void ls() {
        System.out.println("Directory Name : " + currentDir.name);
        System.out.printf("%-12s%-20s%-5s\n", "Type", "Name", "Size");
        System.out.printf("-------------------------------------\n");
        for (MiniOSFile child : currentDir.children) child.print();
    }

    public void cd(String dirName) {
        if (dirName.equals("..")) {
            if (currentDir.parent == null) System.out.println("This directory is root.");
            else {
                currentDir = currentDir.parent;
                System.out.println("Directory is changed to " + currentDir.name + ".");
            }
            return;
        }
        for (MiniOSFile child : currentDir.children) {
            if (child.name.equals(dirName)) {
                if (child.type.equals("directory")) {
                    currentDir = child;
                    System.out.println("Directory is changed to " + dirName + ".");
                    return;
                } else if (child.type.equals("file")) {
                    System.out.println(dirName + " is file.");
                    return;
                }
            }
        }
        System.out.println(dirName + " is not found.");
    }

    public List<Long> getProgram(String exeName) {
        MiniOSFile file = null;
        for (MiniOSFile child : currentDir.children)
            if (child.name.equals(exeName)) {
                file = child;
                break;
            }
        if (file == null) {
            System.out.println("File not found.");
            return null;
        }
        if (!file.type.equals("file")) {
            System.out.println(exeName + " is not executable.");
            return null;
        }
        List<Long> program = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(STORAGE_PATH));
            for (int idx = 0; idx < file.location; idx++) scanner.nextLine();
            for (int idx = 0; idx < file.size; idx++) program.add(Long.parseLong(scanner.nextLine()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return program;
    }
}
