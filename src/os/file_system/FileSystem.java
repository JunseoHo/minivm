package os.file_system;

import hardware.disk.Disk;

import java.util.LinkedList;
import java.util.List;

public class FileSystem {

    // attributes
    private static final int DIR_ENTRY_SIZE = 16;
    private final DiskDriver diskDriver;
    private int currentDirClusterNumber;
    private int recentModifiedClusterNumber;

    public FileSystem(Disk disk) {
        recentModifiedClusterNumber = currentDirClusterNumber
                = (diskDriver = new DiskDriver(disk)).ROOT_DIR;
    }

    public String mkdir(String name) {
        if (!evaluateName(name)) return "Invalid directory name.";
        if (isExist(name)) return name + " already exists.";
        int newDirClusterNumber = diskDriver.allocate(DIR_ENTRY_SIZE);
        if (newDirClusterNumber == -1) return "Out of disk memory.";
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        DirectoryEntry newDir = new DirectoryEntry(name, 0, 0, -1);
        int endOfClusterChain = endOfClusterChain(superDir);
        if (endOfClusterChain == -1) superDir.startingCluster = newDirClusterNumber;
        else diskDriver.writeFAT(endOfClusterChain, newDirClusterNumber);
        diskDriver.writeData(currentDirClusterNumber, superDir.intValues());
        diskDriver.writeData(newDirClusterNumber, newDir.intValues());
        recentModifiedClusterNumber = newDirClusterNumber;
        return "Directory " + name + " has been created.";
    }

    public String rmdir(String name) {
        if (!evaluateName(name)) return "Invalid directory name.";
        if (!isExist(name)) return "Directory " + name + " is not found.";
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        int[] clusterNumbers = new int[]{-1, superDir.startingCluster};
        while (clusterNumbers[1] != -1) {
            DirectoryEntry subDir = diskDriver.getDirectoryEntry(clusterNumbers[1]);
            if (subDir.name.equals(name)) {
                if (subDir.type != 0) return name + " is not directory.";
                if (subDir.startingCluster != -1) return name + " is not empty.";
                if (clusterNumbers[0] == -1) superDir.startingCluster = diskDriver.readFAT(clusterNumbers[1]);
                else diskDriver.writeFAT(clusterNumbers[0], diskDriver.readFAT(clusterNumbers[1]));
                diskDriver.writeFAT(clusterNumbers[1], 0);
                diskDriver.writeData(currentDirClusterNumber, superDir.intValues());
                recentModifiedClusterNumber = clusterNumbers[1];
                return "Directory " + name + " has been removed.";
            }
            clusterNumbers[1] = diskDriver.readFAT(clusterNumbers[0] = clusterNumbers[1]);
        }
        return "Directory " + name + " has been not found.";
    }

    public String touch(String name) {
        if (!evaluateName(name)) return "Invalid file name.";
        if (isExist(name)) return name + " already exists.";
        int newFileClusterNumber = diskDriver.allocate(DIR_ENTRY_SIZE);
        if (newFileClusterNumber == -1) return "Out of disk memory.";
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        DirectoryEntry newFile = new DirectoryEntry(name, 1, 0, -1);
        int endOfClusterChain = endOfClusterChain(superDir);
        if (endOfClusterChain == -1) superDir.startingCluster = newFileClusterNumber;
        else diskDriver.writeFAT(endOfClusterChain, newFileClusterNumber);
        diskDriver.writeData(currentDirClusterNumber, superDir.intValues());
        diskDriver.writeData(newFileClusterNumber, newFile.intValues());
        recentModifiedClusterNumber = newFileClusterNumber;
        return "File " + name + " has been created.";
    }

    public String rm(String name) {
        if (!evaluateName(name)) return "Invalid file name.";
        if (!isExist(name)) return "File " + name + " is not found.";
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        int[] clusterNumbers = new int[]{-1, superDir.startingCluster};
        while (clusterNumbers[1] != -1) {
            DirectoryEntry file = diskDriver.getDirectoryEntry(clusterNumbers[1]);
            if (file.name.equals(name)) {
                if (file.type != 1) return name + " is not file.";
                if (file.isOpened == 1) return "File " + name + " is opened.";
                diskDriver.free(file.startingCluster);
                if (clusterNumbers[0] == -1) superDir.startingCluster = diskDriver.readFAT(clusterNumbers[1]);
                else diskDriver.writeFAT(clusterNumbers[0], diskDriver.readFAT(clusterNumbers[1]));
                diskDriver.writeFAT(clusterNumbers[1], 0);
                diskDriver.writeData(currentDirClusterNumber, superDir.intValues());
                recentModifiedClusterNumber = clusterNumbers[1];
                return "File " + name + " has been removed.";
            }
            clusterNumbers[1] = diskDriver.readFAT(clusterNumbers[0] = clusterNumbers[1]);
        }
        return "File " + name + " has been not found.";
    }

    public String cd(String name) {
        if (!evaluateName(name)) return "Invalid file name.";
        if (name.equals("..")) currentDirClusterNumber = diskDriver.ROOT_DIR;
        else {
            int clusterNumber = findSubdirClusterNumByName(name);
            if (clusterNumber == -1) return name + " is not found.";
            if (diskDriver.getDirectoryEntry(clusterNumber).type != 0) return name + " is not directory.";
            currentDirClusterNumber = clusterNumber;
        }
        return "Directory changed into " + diskDriver.getDirectoryEntry(currentDirClusterNumber).name;
    }

    public String ls() {
        StringBuilder list = new StringBuilder();
        DirectoryEntry dir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        int clusterNumber = dir.startingCluster;
        while (clusterNumber != -1) {
            list.append(diskDriver.getDirectoryEntry(clusterNumber)).append(diskDriver.readFAT(clusterNumber) == -1 ? "" : "\n");
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return list.toString();
    }

    public String open(String name) {
        if (!evaluateName(name)) return "Invalid file name.";
        int clusterNumber = findSubdirClusterNumByName(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dir = diskDriver.getDirectoryEntry(clusterNumber);
        if (dir.type != 1) return name + " is not file.";
        if (dir.isOpened == 1) return "File " + name + " is already opened.";
        dir.isOpened = 1;
        diskDriver.writeData(clusterNumber, dir.intValues());
        return null;
    }

    public String close(String name) {
        if (!evaluateName(name)) return "Invalid file name.";
        int clusterNumber = findSubdirClusterNumByName(name);
        if (clusterNumber == -1) return "File " + name + " has been not found.";
        DirectoryEntry dir = diskDriver.getDirectoryEntry(clusterNumber);
        if (dir.type != 1) return name + " is not file.";
        if (dir.isOpened == 0) return "File " + name + " is already closed.";
        dir.isOpened = 0;
        diskDriver.writeData(clusterNumber, dir.intValues());
        return null;
    }

    public List<Byte> readContents(String name) {
        if (!evaluateName(name)) return null;
        int clusterNumber = findSubdirClusterNumByName(name);
        if (clusterNumber == -1) return null;
        DirectoryEntry dir = diskDriver.getDirectoryEntry(clusterNumber);
        if (dir.startingCluster == -1) return new LinkedList<>();
        return diskDriver.readContents(dir.startingCluster);
    }

    public void writeContents(String name, List<Byte> contents) {
        if (!evaluateName(name)) return;
        int clusterNumber = findSubdirClusterNumByName(name);
        if (clusterNumber == -1) return;
        DirectoryEntry dir = diskDriver.getDirectoryEntry(clusterNumber);
        diskDriver.free(dir.startingCluster);
        dir.startingCluster = diskDriver.allocate(contents.size());
        diskDriver.writeData(clusterNumber, dir.intValues());
        diskDriver.writeContents(dir.startingCluster, contents);
    }

    public String getRecentChangedFAT() {
        return diskDriver.getImage((recentModifiedClusterNumber - 40) * 4, (recentModifiedClusterNumber + 40) * 4);
    }

    public String getRecentChangedData() {
        return diskDriver.getImage((recentModifiedClusterNumber + diskDriver.FAT_SIZE - 10) * 16,
                ((recentModifiedClusterNumber + 10) + diskDriver.FAT_SIZE) * 16);
    }

    public String saveDiskImage() {
        diskDriver.saveDiskImage();
        return "Disk image is saved.";
    }

    private boolean isExist(String name) {
        if (name.length() > 8 || name.length() < 1) return false;
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        int next = superDir.startingCluster;
        while (next != -1) {
            DirectoryEntry dir = diskDriver.getDirectoryEntry(next);
            if (dir.name.equals(name)) return true;
            next = diskDriver.readFAT(next);
        }
        return false;
    }

    private int findSubdirClusterNumByName(String name) {
        DirectoryEntry superDir = diskDriver.getDirectoryEntry(currentDirClusterNumber);
        int clusterNumber = superDir.startingCluster;
        while (clusterNumber != -1) {
            if (diskDriver.getDirectoryEntry(clusterNumber).name.equals(name)) return clusterNumber;
            clusterNumber = diskDriver.readFAT(clusterNumber);
        }
        return -1;
    }

    private boolean evaluateName(String name) {
        if (name == null) return false;
        char[] arr = name.toCharArray();
        if (arr.length < 1 || arr.length > 8) return false; // invalid length
        for (char c : arr) if (c < 32 || c > 126) return false; // invalid character
        return true;
    }

    private int endOfClusterChain(DirectoryEntry directoryEntry) {
        if (directoryEntry.startingCluster == -1) return -1;
        int clusterNumber = directoryEntry.startingCluster;
        while (diskDriver.readFAT(clusterNumber) != -1) clusterNumber = diskDriver.readFAT(clusterNumber);
        return clusterNumber;
    }

}
