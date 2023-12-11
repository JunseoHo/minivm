package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MiniVMUtils {

    public static final String ALLOCATED = "ALLOCATED";
    public static final String FREE = "FREE";
    public static final String READ = "READ";
    public static final String WRITE = "WRITE";

    public static boolean sleep(int millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static List<Byte> readBytesFromFile(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(System.getProperty("user.dir") + path));
            List<Byte> byteList = new ArrayList<>();
            for (Byte b : bytes) byteList.add(b);
            return byteList;
        } catch (IOException e) {
            return null;
        }
    }


}
