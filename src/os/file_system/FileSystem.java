package os.file_system;

import hardware.hdd.HDD;

public class FileSystem {
    // attributes
    private static final int CLUSTER_SIZE = 8;
    private FATEntry[] FAT;
    private Cluster[] clusters = null;
    private int dataRegion;
    // hardware
    private HDD hdd;

    public void associate(HDD hdd) {
        this.hdd = hdd;
        int clusterCount = hdd.size() / CLUSTER_SIZE;
        clusters = new Cluster[clusterCount];
        for (int clusterNumber = 0; clusterNumber < clusterCount; clusterNumber++)
            clusters[clusterNumber] = new Cluster(clusterNumber * CLUSTER_SIZE, CLUSTER_SIZE);
        FAT = new FATEntry[dataRegion = clusterCount / 5];
        for (int i = 0; i < dataRegion; i++) FAT[i] = new FATEntry(clusters[i]);
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

    private static class FATEntry {

        private Cluster cluster;

        public FATEntry(Cluster cluster) {
            this.cluster = cluster;
        }

        public int read(int index) {
            long value = cluster.read();
            value >>= (3 - index) * 16L;
            return (int) (value & 65535);
        }

        public void write(int index, int clusterNumber) {
            long value = cluster.read();
            clusterNumber <<= (3 - index) * 16L;
            cluster.write(value + clusterNumber);
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
        FileSystem fileSystem = new FileSystem();
        HDD hdd = new HDD();
        fileSystem.associate(hdd);
    }

}
