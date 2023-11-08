package os.file_system;

import hardware.hdd.HDD;

import java.util.Arrays;

public class FileSystem {
    // attributes
    private static final int CLUSTER_SIZE = 8;
    private Cluster[] clusters = null;
    private int rootDirectoryEntryClusterNumber;
    // hardware
    private HDD hdd;

    public void associate(HDD hdd) {
        this.hdd = hdd;
        int clusterCount = hdd.size() / CLUSTER_SIZE;
        clusters = new Cluster[clusterCount];
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters[clusterNumber] = new Cluster(clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE);
        rootDirectoryEntryClusterNumber = clusters.length / 2;
    }

    public long read(int clusterNumber) {
        return clusters[clusterNumber].read();
    }

    public void write(int clusterNumber, long value) {
        clusters[clusterNumber].write(value);
    }

    public void createFile(int fileName, int[] contents) {

    }

    private class Cluster {

        int physicalBase;
        int size;

        public Cluster(int physicalBase, int size) {
            this.physicalBase = physicalBase;
            this.size = size;
        }

        public long read() {
            Byte[] byteValues = hdd.read(physicalBase, size);
            long longValue = 0;
            for (Byte byteValue : byteValues)
                longValue = (longValue << 8) | byteValue;
            return longValue;
        }

        public void write(long longValue) {
            byte[] byteValues = new byte[size];
            for (int sectorNumber = 7; sectorNumber > -1; sectorNumber--) {
                byteValues[sectorNumber] = (byte) (longValue & 255);
                longValue >>= 8;
            }
            hdd.write(physicalBase, byteValues);
        }

    }

    private static class DirectoryEntry {

        private String name;
        private int extension;
        private int startingCluster;
        private int fileSize;

        public DirectoryEntry(Cluster cluster) {
            long value = cluster.read();
            fileSize = (int) (value & 255);
            value >>= 8;
            startingCluster = (int) (value & 16384);
            value >>= 14;
            extension = (int) (value & 3);
            value >>= 2;
            name = "";
            for (int i = 0; i < 5; i++) {
                char c = (char) (value & 255);
                if (c != 0) name = c + name;
                value >>= 8;
            }
        }

        public long toLongValue() {
            long value = 0;
            for (int i = 0; i < 5; i++) {
                char c = i < name.length() ? name.charAt(i) : 0;
                value += c;
                value <<= 8;
            }
            value += extension;
            value <<= 2;
            value += startingCluster;
            value <<= 14;
            value += fileSize;
            return value;
        }

    }

    public static void main(String[] args) {
        HDD hdd = new HDD();
        System.out.println(hdd.read(480));
    }

}
