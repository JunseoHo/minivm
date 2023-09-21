package os.file_manager;

import java.io.File;

public class FileManager {

    private static final String ROOT_PATH = "file/";
    private File currentDir;

    public FileManager() {
        this.currentDir = new File(ROOT_PATH);
    }

    public String ls() {
        StringBuilder stringBuilder = new StringBuilder();
        File[] fileNames = currentDir.listFiles();
        if (fileNames == null) return "Empty directory.";
        stringBuilder.append("[File list]");
        for (File file : fileNames) {
            stringBuilder.append("\n");
            stringBuilder.append(file.isFile() ? "FILE      : " : "DIRECTORY : ");
            stringBuilder.append(file.getName());
        }
        return stringBuilder.toString();
    }

    public String cd(String dirName) {
        StringBuilder stringBuilder = new StringBuilder();
        File newDir = new File(currentDir.getAbsolutePath() + "/" + dirName);
        if (!newDir.exists()) return "Directory is not exist.";
        if (!newDir.isDirectory()) return stringBuilder.append(dirName).append(" is not directory.").toString();
        this.currentDir = newDir;
        return stringBuilder.append("Directory is changed to ").append(dirName).append(".").toString();
    }

    public static void main(String[] args) {
        FileManager fileManager = new FileManager();
        System.out.println(fileManager.ls());
        System.out.println(fileManager.cd("dir4"));
        System.out.println(fileManager.cd("file0"));
        System.out.println(fileManager.cd("dir1"));
        System.out.println(fileManager.ls());
    }


}
