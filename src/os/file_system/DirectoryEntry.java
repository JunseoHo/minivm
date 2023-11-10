package os.file_system;

public class DirectoryEntry {

    public String name;
    public int type;
    public int isOpened;
    public int startingCluster;

    public DirectoryEntry(String name, int type, int startingCluster) {
        this.name = name;
        this.type = type;
        this.isOpened = 0;
        this.startingCluster = startingCluster;
    }

    public int[] intValues() {
        int[] intValues = new int[]{0, 0, 0, 0};
        for (int i = 0; i < 8; i++) {
            intValues[i / 4] <<= 8;
            if (i < name.length()) intValues[i / 4] += name.charAt(i);
        }
        intValues[2] += type;
        intValues[2] <<= 16;
        intValues[2] += isOpened;
        intValues[3] = startingCluster;
        return intValues;
    }

    @Override
    public String toString() {
        return String.format("%-10s%-6s", name, type == 0 ? "DIR" : "FILE");
    }

}