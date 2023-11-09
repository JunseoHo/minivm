package os.file_system;

import hardware.hdd.HDD;

import java.util.Arrays;

public class FileSystem {
    // attributes
    private static final int CLUSTER_SIZE = 16;
    private Cluster[] clusters = null;
    private FATEntry[] FAT;
    private int FATSize;
    // hardware
    private HDD hdd;

    public void associate(HDD hdd) {
        this.hdd = hdd;
        int clusterCount = hdd.size() / CLUSTER_SIZE; // = 32768
        clusters = new Cluster[clusterCount];
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters[clusterNumber] = new Cluster(clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE);
        FATSize = clusterCount / 5;
        FAT = new FATEntry[FATSize];
        for (int i = 0; i < FATSize; i++) FAT[i] = new FATEntry(clusters[i]);
        System.out.println("FAT Size : " + FATSize);
        System.out.println("Cluster Size : " + FATSize * 4);
    }

    public int[] read(int clusterNumber) {
        return clusters[FATSize + clusterNumber].read();
    }

    public void write(int clusterNumber, int[] values) {
        clusters[FATSize + clusterNumber].write(values);
    }

    public int readFAT(int logicalClusterNumber) {
        int physicalClusterNumber = logicalClusterNumber / 4;
        int physicalClusterOffset = logicalClusterNumber % 4;
        return FAT[physicalClusterNumber].read(physicalClusterOffset);
    }

    public void writeFAT(int logicalClusterNumber, int value) {
        int physicalClusterNumber = logicalClusterNumber / 4;
        int physicalClusterOffset = logicalClusterNumber % 4;
        FAT[physicalClusterNumber].write(physicalClusterOffset, value);
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


    private class Cluster {

        int physicalBase;
        int size;

        public Cluster(int physicalBase, int size) {
            this.physicalBase = physicalBase;
            this.size = size;
        }

        public int[] read() {
            Byte[] byteValues = hdd.read(physicalBase, size);
            int[] intValues = new int[4];
            Arrays.fill(intValues, 0);
            for (int i = 0; i < size; i++)
                intValues[i / 4] = (intValues[i / 4] << 8) + byteValues[i];
            return intValues;
        }

        public void write(int[] values) {
            byte[] byteValues = new byte[16];
            for (int i = 0; i < 4; i++) {
                Byte[] byteArray = toByteArray(values[i]);
                for (int j = 0; j < 4; j++) byteValues[i * 4 + j] = byteArray[j];
            }
            hdd.write(physicalBase, byteValues);
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
        FileSystem fileSystem = new FileSystem();
        HDD hdd = new HDD();
    }

}
