package os.file_system;

import hardware.disk.Disk;

import java.util.LinkedList;
import java.util.List;

public class FileSystem {

    private DiskDriver diskDriver = null;
    private int currentDir = 0;
    private int recentChangedCluster = 0;

    public FileSystem(Disk disk) {
        currentDir = (diskDriver = new DiskDriver(disk)).ROOT_DIR;
    }

    public String mkdir(String name) {
        if (name.length() > 8 || name.length() < 1) return "Invalid directory name.";
        if (isExist(name)) return name + " already exists.";
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
        updateRecentChangedCluster(newDirClusterNumber);
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
            }
            prev = next;
            next = diskDriver.readFAT(prev);
        }
        return "Directory " + name + " has been not found.";
    }

    public String touch(String name) {
        if (name.length() > 8 || name.length() < 1) return "Invalid file name.";
        if (isExist(name)) return name + " already exists.";
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
        if (name.length() > 8 || name.length() < 1) return "Invalid file name.";
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        int prev;
        int next = superDir.startingCluster;
        while (next != -1) {
            DirectoryEntry file = diskDriver.dirEntry(next);
            if (file.name.equals(name)) {
                if (file.type != 1) return name + " is not file.";
                diskDriver.free(file.startingCluster);
                diskDriver.free(next);
                return "File " + name + " has been removed.";
            }
            prev = next;
            next = diskDriver.readFAT(prev);
        }
        return "File " + name + " has been not found.";
    }

    public String cd(String name) {
        int clusterNumber = findSubdirEntry(name);
        if (clusterNumber == -1) return name + " is not found.";
        currentDir = clusterNumber;
        return "Directory changed.";
    }

    public String ls() {
        String list = "";
        DirectoryEntry dir = diskDriver.dirEntry(currentDir);
        int clusterNumber = dir.startingCluster;
        while (clusterNumber != -1) {
            list += diskDriver.dirEntry(clusterNumber)
                    + (diskDriver.readFAT(clusterNumber) == -1 ? "" : "\n");
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return list;
    }

    public String open(String name) {
        if (name == null || name.length() > 8 || name.length() < 1) return "Invalid file name.";
        int clusterNumber = findSubdirEntry(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dir = diskDriver.dirEntry(clusterNumber);
        if (dir.type != 1) return name + " is not file.";
        if (dir.isOpened == 1) return "File " + name + " is already opened.";
        dir.isOpened = 1;
        diskDriver.writeData(clusterNumber, dir.intValues());
        return null;
    }

    public String close(String name) {
        int clusterNumber = findSubdirEntry(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dirEntry = diskDriver.dirEntry(clusterNumber);
        if (dirEntry.isOpened == 0) return "File " + name + " is already closed.";
        dirEntry.isOpened = 0;
        diskDriver.writeData(clusterNumber, dirEntry.intValues());
        return null;
    }

    public List<Byte> getContents(String name) {
        int clusterNumber = findSubdirEntry(name);
        DirectoryEntry dirEntry = diskDriver.dirEntry(clusterNumber);
        if (dirEntry == null) return null;
        if (dirEntry.startingCluster == -1) return new LinkedList<>();
        return diskDriver.readContents(dirEntry.startingCluster);
    }

    public boolean setContents(String name, List<Byte> contents) {
        int clusterNumber = findSubdirEntry(name);
        if (clusterNumber == -1) return false;
        DirectoryEntry dir = diskDriver.dirEntry(findSubdirEntry(name));
        int startingCluster = diskDriver.allocate(contents.size());
        diskDriver.free(dir.startingCluster);
        dir.startingCluster = startingCluster;
        diskDriver.writeData(clusterNumber, dir.intValues());
        diskDriver.writeContents(startingCluster, contents);
        return true;
    }

    public String getRecentChangedFAT() {
        return diskDriver.getImage(recentChangedCluster * 16, (recentChangedCluster + 18) * 16);
    }

    public String getRecentChangedData() {
        return diskDriver.getImage((recentChangedCluster + diskDriver.FAT_SIZE) * 16,
                ((recentChangedCluster + 18) + diskDriver.FAT_SIZE) * 16);
    }

    private boolean isExist(String name) {
        if (name.length() > 8 || name.length() < 1) return false;
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        int next = superDir.startingCluster;
        while (next != -1) {
            DirectoryEntry dir = diskDriver.dirEntry(next);
            if (dir.name.equals(name)) return true;
            next = diskDriver.readFAT(next);
        }
        return false;
    }

    private int findSubdirEntry(String name) {
        DirectoryEntry superDir = diskDriver.dirEntry(currentDir);
        int clusterNumber = superDir.startingCluster;
        while (clusterNumber != -1) {
            if (diskDriver.dirEntry(clusterNumber).name.equals(name)) return clusterNumber;
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return -1;
    }

    private void updateRecentChangedCluster(int clusterNumber) {
        if (clusterNumber < recentChangedCluster || clusterNumber > recentChangedCluster + 37)
            recentChangedCluster = clusterNumber;
    }

}
