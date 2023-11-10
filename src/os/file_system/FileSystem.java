package os.file_system;

import hardware.hdd.Disk;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    // components
    private DiskDriver diskDriver = null;
    private int currentDir = 0;


    public FileSystem(Disk disk) {
        diskDriver = new DiskDriver(disk);
        currentDir = diskDriver.ROOT_DIR;
    }

    public String mkdir(String name) {
        return null;
    }

    public String rmdir(String name) {
        return null;
    }

    public String touch(String name) {
        return null;
    }

    public String rm(String name) {
        return null;
    }

    public String cd(String name){
        return null;
    }

    public String open(String name) {
        return null;
    }

    public String overwrite(String name, String contents) {
        return null;
    }

    public static void main(String[] args) {
        Disk disk = new Disk();
        DiskDriver diskDriver = new DiskDriver(disk);
        int ptr = diskDriver.allocate(100);
        List<Byte> byteList = new ArrayList<>();
        byteList.add((byte) 'H');
        byteList.add((byte) 'E');
        byteList.add((byte) 'L');
        byteList.add((byte) 'L');
        byteList.add((byte) 'O');
        byteList.add((byte) 'W');
        byteList.add((byte) 'O');
        byteList.add((byte) 'R');
        byteList.add((byte) 'L');
        byteList.add((byte) 'D');
        byteList.add((byte) 'H');
        byteList.add((byte) 'E');
        byteList.add((byte) 'L');
        byteList.add((byte) 'L');
        byteList.add((byte) 'O');
        byteList.add((byte) 'W');
        byteList.add((byte) 'O');
        byteList.add((byte) 'R');
        byteList.add((byte) 'L');
        byteList.add((byte) 'D');
        diskDriver.write(ptr, byteList);
        diskDriver.free(ptr);
        System.out.println(diskDriver.read(ptr));
        System.out.println(disk.dump(0, 100));
        System.out.println(disk.dump(104848, 104948));
    }

}
