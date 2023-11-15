package os.file_system;

import hardware.disk.Disk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DiskDriver {
    // attributes
    public final int CLUSTER_SIZE = 16;
    public final int ROOT_DIR = 0;
    public final int FAT_SIZE;
    // hardware
    private Disk disk = null;
    // components
    private List<Cluster> clusters = null;

    public void save() {
        disk.save();
    }

    private class Cluster {
        public int number;
        public int base;
        public int size;

        public Cluster(int number, int base, int size) {
            this.number = number;
            this.base = base;
            this.size = size;
        }

        public int[] read() {
            Byte[] byteValues = disk.read(base, size);
            if (byteValues == null) return null;
            int[] intValues = new int[]{0, 0, 0, 0};
            for (int i = 0; i < size; i++) intValues[i / 4] = (intValues[i / 4] << 8) | byteValues[i];
            return intValues;
        }

        public boolean write(int[] intValues) {
            if (intValues == null) return false;
            byte[] byteValues = new byte[size];
            for (int i = 0; i < 4; i++) {
                Byte[] byteArray = toByteArray(intValues[i]);
                for (int j = 0; j < 4; j++) byteValues[i * 4 + j] = byteArray[j];
            }
            return disk.write(base, byteValues);
        }
    }

    public DiskDriver(Disk disk) {
        // set associations
        this.disk = disk;
        // create clusters
        int clusterCount = disk.size() / CLUSTER_SIZE;
        clusters = new ArrayList<>();
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters.add(new Cluster(clusterNumber, clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE));
        FAT_SIZE = clusterCount / 5;
    }

    public int readFAT(int clusterNumber) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return 0;
        return clusters.get(clusterNumber / 4).read()[clusterNumber % 4];
    }

    public boolean writeFAT(int clusterNumber, int value) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return false;
        Cluster cluster = clusters.get(clusterNumber / 4);
        int[] values = cluster.read();
        values[clusterNumber % 4] = value;
        return cluster.write(values);
    }

    public int[] readData(int clusterNumber) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return null;
        return clusters.get(FAT_SIZE + clusterNumber).read();
    }

    public boolean writeData(int clusterNumber, int[] values) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE || values == null) return false;
        return clusters.get(FAT_SIZE + clusterNumber).write(values);
    }

    public List<Byte> readContents(int clusterNumber) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return null;
        List<Byte> contents = new ArrayList<>();
        while (clusterNumber != -1) {
            int[] data = readData(clusterNumber);
            for (int d : data) contents.addAll(Arrays.asList(toByteArray(d)));
            clusterNumber = readFAT(clusterNumber);
        }
        return contents;
    }

    public boolean writeContents(int clusterNumber, List<Byte> contents) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE || contents == null) return false;
        List<Integer> intArray = toIntArray(contents);
        while (intArray.size() % 4 != 0) intArray.add(0);
        for (int i = 0; i < intArray.size() / 4; i++) {
            int[] data = new int[4];
            for (int j = 0; j < 4; j++) data[j] = intArray.get(i * 4 + j);
            writeData(clusterNumber, data);
            if ((clusterNumber = readFAT(clusterNumber)) == -1) break;
        }
        return true;
    }

    public int allocate(int size) {
        if (size < 1) return -1;
        int requiredClusterCount = (size / CLUSTER_SIZE) + ((size % CLUSTER_SIZE == 0) ? 0 : 1);
        List<Integer> allocated = new LinkedList<>();
        for (int clusterNumber = 0; clusterNumber < FAT_SIZE; clusterNumber++) {
            if (allocated.size() < requiredClusterCount && readFAT(clusterNumber) == 0)
                allocated.add(clusterNumber);
            if (allocated.size() == requiredClusterCount) break;
        }
        if (allocated.size() < requiredClusterCount) return -1; // out of memory
        for (int index = 0; index < allocated.size(); index++)
            writeFAT(allocated.get(index), index == allocated.size() - 1 ? -1 : allocated.get(index + 1));
        return allocated.get(0);
    }

    public void free(int clusterNumber) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return;
        int next = readFAT(clusterNumber);
        if (next == 0) return;
        while (clusterNumber != -1 && next != -1) {
            writeFAT(clusterNumber, 0);
            next = readFAT(clusterNumber = next);
        }
        writeFAT(clusterNumber, 0);
    }

    public DirectoryEntry getDirectoryEntry(int clusterNumber) {
        if (clusterNumber < 0 || clusterNumber > FAT_SIZE) return null;
        int[] values = clusters.get(FAT_SIZE + clusterNumber).read();
        String name = "";
        for (int i = 1; i > -1; i--)
            for (int j = 0; j < 4; j++) {
                char c = (char) (values[i] & 0xFF);
                if (c != 0) name = c + name;
                values[i] >>= 8;
            }
        int isOpened = values[2] & 0xF;
        values[2] >>= 16;
        int type = values[2] & 0xF;
        int startingCluster = values[3];
        return new DirectoryEntry(name, type, isOpened, startingCluster);
    }

    private Byte[] toByteArray(int i) {
        Byte[] byteArray = new Byte[4];
        for (int index = 3; index > -1; index--) {
            byteArray[index] = (byte) (i & 0xFF);
            i >>= 8;
        }
        return byteArray;
    }

    private List<Integer> toIntArray(List<Byte> byteArray) {
        while (byteArray.size() % 4 != 0) byteArray.add((byte) 0);
        List<Integer> intArray = new ArrayList<>();
        for (int i = 0; i < byteArray.size() / 4; i++) {
            int value = 0;
            for (int j = 0; j < 4; j++) {
                value <<= 8;
                value |= byteArray.get(i * 4 + j);
            }
            intArray.add(value);
        }
        return intArray;
    }

    public String getImage(int begin, int end) {
        return disk.getImage(begin, end);
    }

}
