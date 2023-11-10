package os.file_system;

import hardware.hdd.Disk;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    private DiskDriver diskDriver = null;
    private int currentDir = 0;

    public FileSystem(Disk disk) {
        diskDriver = new DiskDriver(disk);
        currentDir = diskDriver.ROOT_DIR;
    }

    public String mkdir(String name) {
        if (name.length() > 8 || name.length() < 1) return "Invalid directory name.";
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        DirectoryEntry newDir = new DirectoryEntry(name, 0, -1);
        int newDirClusterNumber = diskDriver.allocate(16);
        if (superDir.startingCluster == -1) superDir.startingCluster = newDirClusterNumber;
        else {
            int lastCluster = superDir.startingCluster;
            while (diskDriver.readFAT(lastCluster) != -1) lastCluster = diskDriver.readFAT(lastCluster);
            diskDriver.writeFAT(lastCluster, newDirClusterNumber);
        }
        diskDriver.writeData(currentDir, superDir.intValues());
        diskDriver.writeData(newDirClusterNumber, newDir.intValues());
        return "Directory " + name + " has been created.";
    }

    public String rmdir(String name) {
        if (name.length() > 8 || name.length() < 1) return "Invalid directory name.";
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        int prev = -1;
        int next = superDir.startingCluster;
        while (next != -1) {
            DirectoryEntry dir = diskDriver.dirEntry(next);
            if (dir.name.equals(name)) {
                if (dir.type != 0) return name + " is not directory.";
                if (prev == -1) superDir.startingCluster = diskDriver.readFAT(next);
                else diskDriver.writeFAT(prev, diskDriver.readFAT(next));
                diskDriver.writeFAT(next, 0);
                diskDriver.writeData(currentDir, superDir.intValues());
                return "Directory " + name + " has been removed.";
            } else {
                prev = next;
                next = diskDriver.readFAT(prev);
            }
        }
        return "Directory " + name + " has been not found.";
    }

    public String touch(String name) {
        if (name.length() > 8 || name.length() < 1) return "Invalid file name.";
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        DirectoryEntry newFile = new DirectoryEntry(name, 1, -1);
        int newFileClusterNumber = diskDriver.allocate(16);
        if (superDir.startingCluster == -1) superDir.startingCluster = newFileClusterNumber;
        else {
            int lastCluster = superDir.startingCluster;
            while (diskDriver.readFAT(lastCluster) != -1) lastCluster = diskDriver.readFAT(lastCluster);
            diskDriver.writeFAT(lastCluster, newFileClusterNumber);
        }
        diskDriver.writeData(currentDir, superDir.intValues());
        diskDriver.writeData(newFileClusterNumber, newFile.intValues());
        return "File " + name + " has been created.";
    }

    public String rm(String name) {
        return null;
    }

    public String cd(String name) {
        int clusterNumber = findClusterNumber(name);
        if (clusterNumber == -1) return name + " is not found.";
        currentDir = clusterNumber;
        return "Directory changed.";
    }

    public String ls() {
        String list = "";
        DirectoryEntry dir = diskDriver.dirEntry(currentDir);
        int clusterNumber = dir.startingCluster;
        while (clusterNumber != -1) {
            list += diskDriver.dirEntry(clusterNumber) + "\n";
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return list;
    }

    public String open(String name) {
        int clusterNumber = findClusterNumber(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dirEntry = diskDriver.dirEntry(clusterNumber);
        if (dirEntry.isOpened == 1) return "File " + name + " is already opened.";
        dirEntry.isOpened = 1;
        diskDriver.writeData(clusterNumber, dirEntry.intValues());
        return "File " + name + " has been opened.";
    }

    public String close(String name) {
        int clusterNumber = findClusterNumber(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dirEntry = diskDriver.dirEntry(clusterNumber);
        if (dirEntry.isOpened == 0) return "File " + name + " is already closed.";
        dirEntry.isOpened = 0;
        diskDriver.writeData(clusterNumber, dirEntry.intValues());
        return "File " + name + " has been closed.";
    }

    public String overwrite(String name, String contents) {
        return null;
    }

    private int findClusterNumber(String name) {
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        int clusterNumber = superDir.startingCluster;
        while (clusterNumber != -1) {
            DirectoryEntry dir = diskDriver.dirEntry(clusterNumber);
            if (dir.name.equals(name)) return clusterNumber;
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return -1;
    }

    public static void main(String[] args) {
        Disk disk = new Disk();
        FileSystem fileSystem = new FileSystem(disk);
        System.out.println(fileSystem.mkdir("Hello"));
        System.out.println(fileSystem.mkdir("ByeBye"));
        System.out.println(fileSystem.mkdir("World"));
        System.out.println(fileSystem.touch("OKOK"));
        System.out.println(fileSystem.ls());
        System.out.println(fileSystem.rmdir("ByeBye"));
        System.out.println(fileSystem.ls());
        System.out.println(disk.dump(0, 100));
        System.out.println(disk.dump(104848, 104948));
    }

}
