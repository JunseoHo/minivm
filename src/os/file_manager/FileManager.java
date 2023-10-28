package os.file_manager;

import common.InterruptServiceRoutine;
import common.logger.MiniVMLogger;
import hardware.io_device.IODevice;
import hardware.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import os.OSModule;
import os.SIRQ;
import os.SWName;

import java.io.FileReader;
import java.io.IOException;

public class FileManager extends OSModule {
    // attributes
    private static final String FILE_METADATA_PATH = "/src/os/file_manager/FILE_METADATA.json";
    private File currentDir;
    // hardware
    private Storage storage;

    public FileManager() {
        super();
        registerISR(SIRQ.REQUEST_FILE, this::getFile);
        registerISR(SIRQ.REQUEST_CHANGE_DIRECTORY, this::changeDirectory);
    }

    @Override
    public void associate(IODevice ioDevice) {
        storage = (Storage) ioDevice;
        importFileIndex();
    }

    @InterruptServiceRoutine
    public void getFile(SIRQ intr) {
        String fileName = (String) intr.values()[0];
        for (File file : currentDir.children)
            if (file.name.equals(fileName)) {
                send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.RESPONSE_FILE, file));
                return;
            }
        send(new SIRQ(SWName.PROCESS_MANAGER, SIRQ.FILE_NOT_FOUND));
    }

    @InterruptServiceRoutine
    public void changeDirectory(SIRQ intr) {
        String dirName = (String) intr.values()[0];
        if (dirName == null) return;
        if (dirName.equals("..")) {
            if (currentDir.parent == null) MiniVMLogger.error("FileManager", "Current directory is root.");
            else currentDir = currentDir.parent;
        } else {
            for (File child : currentDir.children) {
                if (child.name.equals(dirName)) {
                    if (child.type != FileType.DIRECTORY)
                        MiniVMLogger.error("FileManager", dirName + " is not directory.");
                    else {
                        currentDir = child;
                        return;
                    }
                }
            }
            MiniVMLogger.error("FileManager", dirName + " is not found.");
        }
    }

    private File createFileTree(JSONObject obj, File parent) {
        String type = (String) obj.get("type");
        File file = null;
        if (type.equals("directory")) {
            file = new File(FileType.DIRECTORY);
            file.name = (String) obj.get("name");
            JSONArray files = (JSONArray) obj.get("files");
            for (int index = 0; index < files.size(); index++)
                file.addChild(createFileTree((JSONObject) files.get(index), file));
        } else if (type.equals("executable")) {
            file = new File(FileType.EXECUTABLE);
            file.name = (String) obj.get("name");
            file.base = (long) obj.get("base");
            file.size = (long) obj.get("size");
            for (int index = 0; index < file.size; index++)
                file.addRecord(storage.readBuffer((int) (file.base + index)));
        } else if (type.equals("data")) {
            file = new File(FileType.DATA);
            file.name = (String) obj.get("name");
            file.base = (long) obj.get("base");
            file.size = (long) obj.get("size");
            for (int index = 0; index < file.size; index++)
                file.addRecord(storage.readBuffer((int) (file.base + index)));
        }
        file.parent = parent;
        return file;
    }

    private void importFileIndex() {
        try {
            JSONObject fileMetadata = (JSONObject) new JSONParser()
                    .parse(new FileReader(System.getProperty("user.dir") + FILE_METADATA_PATH));
            currentDir = createFileTree(fileMetadata, null);
        } catch (IOException | ParseException e) {
            System.err.println("Failed to import file metadata.");
        }
    }

    @Override
    public String toString() {
        String str = "[File Manager]\nCurrent directory : " + currentDir.name + "\n\n";
        for (File child : currentDir.children) str += child.name + " (" + child.type + ")\n";
        return str;
    }

}
