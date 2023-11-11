package os.file_system;

import hardware.disk.Disk;

public class FileSystemOld {
    // attributes
    private static final int CLUSTER_SIZE = 16;
    // components
    private Cluster[] clusters = null;
    private FATEntry[] fileAllocationTable;
    private int rootDirectoryEntry;
    private int currentDirectoryEntry;
    // hardware
    private Disk disk;

    public void associate(Disk disk) {
        this.disk = disk;
        // create cluster list & file allocation table
        int clusterCount = disk.size() / CLUSTER_SIZE;
        clusters = new Cluster[clusterCount];
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters[clusterNumber] = new Cluster(clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE);
        fileAllocationTable = new FATEntry[clusterCount / 5];
        for (int i = 0; i < fileAllocationTable.length; i++) fileAllocationTable[i] = new FATEntry(clusters[i]);
        rootDirectoryEntry = 0;
        currentDirectoryEntry = 0;
        //mkRootDir();
    }

    public boolean mkdir(String name) {
        // create directory entry
        if (name.length() > 8 || name.length() < 1) return false; // name length is invalid
        DirectoryEntry newDir = new DirectoryEntry(name, 0, 0, -1);
        int allocatedClusterNumber = allocateEmptyCluster();
        write(allocatedClusterNumber, newDir.toIntArray());
        DirectoryEntry currentDir = new DirectoryEntry(clusters[fileAllocationTable.length + currentDirectoryEntry]);
        ++currentDir.fileSize;
        if (currentDir.startingClusterNumber == -1) currentDir.startingClusterNumber = allocatedClusterNumber;
        else {
            int endOfChain = currentDir.startingClusterNumber;
            while (readFAT(endOfChain) != -1) endOfChain = readFAT(endOfChain);
            writeFAT(endOfChain, allocatedClusterNumber);
        }
        write(currentDirectoryEntry, currentDir.toIntArray());
        return true;
    }

    public boolean rmdir(String name) {
        return false;
    }

    public boolean createFile(String name) {
        if (name.length() > 8 || name.length() < 1) return false; // name length is invalid
        DirectoryEntry newFile = new DirectoryEntry(name, 1, 0, -1);
        int allocatedClusterNumber = allocateEmptyCluster();
        System.out.println("Allocated = " + allocatedClusterNumber);
        write(allocatedClusterNumber, newFile.toIntArray());
        DirectoryEntry currentDir = new DirectoryEntry(clusters[fileAllocationTable.length + currentDirectoryEntry]);
        ++currentDir.fileSize;
        if (currentDir.startingClusterNumber == -1) currentDir.startingClusterNumber = allocatedClusterNumber;
        else {
            int endOfChain = currentDir.startingClusterNumber;
            while (readFAT(endOfChain) != -1) endOfChain = readFAT(endOfChain);
            writeFAT(endOfChain, allocatedClusterNumber);
        }
        write(currentDirectoryEntry, currentDir.toIntArray());
        return true;
    }

    public boolean updateFile(String name, byte[] contents) {
        DirectoryEntry dirEntry = findDirectoryEntry(name);
        if (dirEntry == null) return false; // file not found
        if (dirEntry.extension != 1) return false; // is not file
        int requiredClusterCount = (contents.length / CLUSTER_SIZE) + 1;
        int allocatedClusterCount = getAllocatedClusterCount(dirEntry);
        freeClusters(dirEntry);

        return false;
    }

    public void freeClusters(DirectoryEntry dirEntry) {
        int endOfChain = dirEntry.startingClusterNumber;
        if (endOfChain == -1) return;
        int nextChain = readFAT(endOfChain);
        do {
            writeFAT(endOfChain, 0);
            endOfChain = nextChain;
            if (endOfChain == -1) break;
            nextChain = readFAT(endOfChain);
        } while (endOfChain != -1);
        dirEntry.startingClusterNumber = -1;
        dirEntry.fileSize = 0;
    }

    public int getAllocatedClusterCount(DirectoryEntry dirEntry) {
        int endOfChain = dirEntry.startingClusterNumber;
        int clusterCount = 0;
        while (endOfChain != -1) {
            ++clusterCount;
            endOfChain = readFAT(endOfChain);
        }
        return clusterCount;
    }

    private DirectoryEntry findDirectoryEntry(String name) {
        DirectoryEntry currentDir = new DirectoryEntry(clusters[fileAllocationTable.length + currentDirectoryEntry]);
        int childrenClusterNumber = currentDir.startingClusterNumber;
        while (childrenClusterNumber != -1) {
            DirectoryEntry dir = new DirectoryEntry(clusters[fileAllocationTable.length + childrenClusterNumber]);
            if (dir.name.equals(name)) return dir;
            childrenClusterNumber = readFAT(childrenClusterNumber);
        }
        return null;
    }

    public String list() {
        String list = "";
        DirectoryEntry currentDir = new DirectoryEntry(clusters[fileAllocationTable.length + currentDirectoryEntry]);
        int endOfChain = currentDir.startingClusterNumber;
        if (endOfChain == -1) return list;
        while (endOfChain != -1) {
            DirectoryEntry child = new DirectoryEntry(clusters[fileAllocationTable.length + endOfChain]);
            list += String.format("%-10s%-10s%-3d\n", child.name, child.extension == 0 ? "Dir" : "File", child.fileSize);
            endOfChain = readFAT(endOfChain);
        }
        return list;
    }

    private int[] read(int clusterNumber) {
        return clusters[fileAllocationTable.length + clusterNumber].read();
    }

    private void write(int clusterNumber, int[] values) {
        clusters[fileAllocationTable.length + clusterNumber].write(values);
    }

    private int readFAT(int logicalClusterNumber) {
        int physicalClusterNumber = logicalClusterNumber / 4;
        int physicalClusterOffset = logicalClusterNumber % 4;
        return fileAllocationTable[physicalClusterNumber].read(physicalClusterOffset);
    }

    private void writeFAT(int logicalClusterNumber, int value) {
        int physicalClusterNumber = logicalClusterNumber / 4;
        int physicalClusterOffset = logicalClusterNumber % 4;
        fileAllocationTable[physicalClusterNumber].write(physicalClusterOffset, value);
    }

    private boolean changeCurrentDirectory(String name) {
        if (name.equals("..")) {
            currentDirectoryEntry = rootDirectoryEntry;
            return true;
        }
        DirectoryEntry currentDir = new DirectoryEntry(clusters[fileAllocationTable.length + currentDirectoryEntry]);
        int childrenClusterNumber = currentDir.startingClusterNumber;
        while (childrenClusterNumber != -1) {
            DirectoryEntry dir = new DirectoryEntry(clusters[fileAllocationTable.length + childrenClusterNumber]);
            if (dir.name.equals(name)) {
                currentDirectoryEntry = childrenClusterNumber;
                return true;
            }
            childrenClusterNumber = readFAT(childrenClusterNumber);
        }
        return false;
    }

    private int allocateEmptyCluster() {
        for (int entryNumber = 0; entryNumber < fileAllocationTable.length; entryNumber++)
            for (int offset = 0; offset < 4; offset++) {
                if (fileAllocationTable[entryNumber].read(offset) == 0) {
                    fileAllocationTable[entryNumber].write(offset, -1);
                    return entryNumber * 4 + offset;
                }
            }
        return -1;
    }

    private class Cluster {

        int physicalBase;
        int size;

        public Cluster(int physicalBase, int size) {
            this.physicalBase = physicalBase;
            this.size = size;
        }

        public int[] read() {
            Byte[] byteValues = disk.read(physicalBase, size);
            int[] intValues = new int[]{0, 0, 0, 0};
            for (int i = 0; i < size; i++) intValues[i / 4] = (intValues[i / 4] << 8) | byteValues[i];
            return intValues;
        }

        public void write(int[] values) {
            byte[] byteValues = new byte[16];
            for (int i = 0; i < 4; i++) {
                Byte[] byteArray = toByteArray(values[i]);
                for (int j = 0; j < 4; j++) byteValues[i * 4 + j] = byteArray[j];
            }
            disk.write(physicalBase, byteValues);
        }

    }

    private class FATEntry {

        private final Cluster cluster;

        public FATEntry(Cluster cluster) {
            this.cluster = cluster;
        }

        public int read(int offset) {
            return cluster.read()[offset];
        }

        public void write(int offset, int value) {
            int[] values = cluster.read();
            values[offset] = value;
            cluster.write(values);
        }
    }

    private class DirectoryEntry {

        public String name;
        public int extension; // 0 = directory, 1 = executable, 2 = file
        public int fileSize;
        public int startingClusterNumber;

        public DirectoryEntry(Cluster cluster) {
            int[] values = cluster.read();
            name = "";
            for (int i = 1; i > -1; i--)
                for (int j = 0; j < 4; j++) {
                    char c = (char) (values[i] & 0xFF);
                    if (c != 0) name = c + name;
                    values[i] >>= 8;
                }
            fileSize = values[2] & 0xFFFF;
            values[2] >>= 16;
            extension = values[2] & 0xFFFF;
            startingClusterNumber = values[3];
        }

        public DirectoryEntry(String name, int extension, int fileSize, int startingClusterNumber) {
            this.name = name;
            this.extension = extension;
            this.fileSize = fileSize;
            this.startingClusterNumber = startingClusterNumber;
        }

        public int[] toIntArray() {
            int[] intArray = new int[]{0, 0, 0, 0};
            for (int i = 0; i < 8; i++) {
                if (i < name.length()) intArray[i / 4] += name.charAt(i);
                if (i % 4 != 3) intArray[i / 4] <<= 8;
            }
            intArray[2] += extension;
            intArray[2] <<= 16;
            intArray[2] += fileSize;
            intArray[3] = startingClusterNumber;
            return intArray;
        }

    }

    private Byte[] toByteArray(int i) {
        Byte[] byteArray = new Byte[4];
        for (int index = 3; index > -1; index--) {
            byteArray[index] = (byte) (i & 0xFF);
            i >>= 8;
        }
        return byteArray;
    }

    public static void main(String[] args) {
        FileSystemOld fileSystem = new FileSystemOld();
        Disk disk = new Disk();
        fileSystem.associate(disk);
        fileSystem.mkdir("hello");
        fileSystem.mkdir("byebye");
        fileSystem.mkdir("meowmoew");
        System.out.println(fileSystem.list());
        System.out.println(disk.getImage(0, 100));
        System.out.println(disk.getImage(104848, 104948));
    }

}
