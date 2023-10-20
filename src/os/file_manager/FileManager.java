package os.file_manager;

import hardware.io_device.IODevice;
import hardware.storage.Storage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import os.OSModule;
import os.SIQ;
import os.SWName;

import java.io.FileReader;
import java.io.IOException;

public class FileManager extends OSModule {
    // attributes
    private static final String FILE_METADATA_PATH = "/src/os/file_manager/FILE_METADATA.json";
    private File currentDir;
    // hardware
    private Storage storage;

    @Override
    public void associate(IODevice ioDevice) {
        storage = (Storage) ioDevice;
        importFileIndex();
    }

    @Override
    public void run() {
        while (true) handleInterrupt();
    }

    @Override
    public void handleInterrupt() {
        for (SIQ intr : receiveAll()) queue.enqueue(intr);
        while (!queue.isEmpty()) {
            SIQ intr = queue.dequeue();
            switch (intr.id) {
                case SIQ.REQUEST_FILE -> getFile((String) intr.values[0]);
            }
        }
    }

    public File getCurrentDir() {
        return currentDir;
    }

    private File createFileTree(JSONObject obj) {
        String type = (String) obj.get("type");
        File file = null;
        if (type.equals("directory")) {
            file = new File(FileType.DIRECTORY);
            file.name = (String) obj.get("name");
            JSONArray files = (JSONArray) obj.get("files");
            for (int index = 0; index < files.size(); index++)
                file.addChild(createFileTree((JSONObject) files.get(index)));
        } else if (type.equals("executable")) {
            file = new File(FileType.EXECUTABLE);
            file.name = (String) obj.get("name");
            file.base = (long) obj.get("base");
            file.size = (long) obj.get("size");
            for (int index = 0; index < file.size; index++)
                file.addRecord(storage.readRecord((int) (file.base + index)));
        } else if (type.equals("data")) {
            file = new File(FileType.DATA);
            file.name = (String) obj.get("name");
            file.base = (long) obj.get("base");
            file.size = (long) obj.get("size");
            for (int index = 0; index < file.size; index++)
                file.addRecord(storage.readRecord((int) (file.base + index)));
        }
        return file;
    }

    private void importFileIndex() {
        try {
            JSONObject fileMetadata = (JSONObject) new JSONParser().parse(new FileReader(System.getProperty("user.dir") + FILE_METADATA_PATH));
            currentDir = createFileTree(fileMetadata);
        } catch (IOException | ParseException e) {
            System.err.println("failed to import file metadata.");
        }
    }

    public void getFile(String fileName) {
        for (File file : currentDir.children)
            if (file.name.equals(fileName)) {
                send(new SIQ(SWName.PROCESS_MANAGER, SIQ.RESPONSE_FILE, file));
                return;
            }
        send(new SIQ(SWName.PROCESS_MANAGER, SIQ.FILE_NOT_FOUND));
    }
}
