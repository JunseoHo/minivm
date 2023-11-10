package os.file_system;

import hardware.hdd.Disk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DiskDriver {
    // attributes
    final int CLUSTER_SIZE = 16;
    final int ROOT_DIR = 0;
    final int FAT_SIZE;
    // hardware
    private Disk disk = null;
    // components
    private List<Cluster> clusters = null;

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
            int[] intValues = new int[]{0, 0, 0, 0};
            for (int i = 0; i < size; i++) intValues[i / 4] = (intValues[i / 4] << 8) | byteValues[i];
            return intValues;
        }

        public void write(int[] intValues) {
            byte[] byteValues = new byte[size];
            for (int i = 0; i < 4; i++) {
                Byte[] byteArray = toByteArray(intValues[i]);
                for (int j = 0; j < 4; j++) byteValues[i * 4 + j] = byteArray[j];
            }
            disk.write(base, byteValues);
        }
    }

    public DiskDriver(Disk disk) {
        // associate hardware
        this.disk = disk;
        // create clusters
        int clusterCount = disk.size() / CLUSTER_SIZE;
        clusters = new ArrayList<>();
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters.add(new Cluster(clusterNumber, clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE));
        FAT_SIZE = clusterCount / 5;
    }

    public int readFAT(int clusterNumber) {
        return clusters.get(clusterNumber / 4).read()[clusterNumber % 4];
    }

    public void writeFAT(int clusterNumber, int value) {
        Cluster cluster = clusters.get(clusterNumber / 4);
        int[] values = cluster.read();
        values[clusterNumber % 4] = value;
        cluster.write(values);
    }

    public void write(int clusterNumber, List<Byte> byteData) {
        List<Integer> intData = toIntArray(byteData);
        while (intData.size() % 4 != 0) intData.add(0);
        for (int i = 0; i < intData.size() / 4; i++) {
            int[] data = new int[4];
            data[0] = intData.get(i * 4);
            data[1] = intData.get(i * 4 + 1);
            data[2] = intData.get(i * 4 + 2);
            data[3] = intData.get(i * 4 + 3);
            writeData(clusterNumber, data);
            if ((clusterNumber = readFAT(clusterNumber)) == -1) break;
        }
    }

    public List<Byte> read(int clusterNumber) {
        List<Byte> byteData = new ArrayList<>();
        while (clusterNumber != -1) {
            int[] data = readData(clusterNumber);
            for (int d : data) for (byte b : toByteArray(d)) byteData.add(b);
            clusterNumber = readFAT(clusterNumber);
        }
        return byteData;
    }

    public int[] readData(int clusterNumber) {
        return clusters.get(FAT_SIZE + clusterNumber).read();
    }

    public void writeData(int clusterNumber, int[] values) {
        clusters.get(FAT_SIZE + clusterNumber).write(values);
    }

    public int allocate(int size) {
        int requiredClusterCount = (size / CLUSTER_SIZE) + 1;
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
        int next = readFAT(clusterNumber);
        if (next == 0) return;
        while (clusterNumber != -1 && next != -1) {
            writeFAT(clusterNumber, 0);
            next = readFAT(clusterNumber = next);
        }
        writeFAT(clusterNumber, 0);
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

}
