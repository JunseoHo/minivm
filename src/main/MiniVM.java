package main;

import hardware.hdd.Disk;
import os.OperatingSystem;
import os.SystemCall;
import os.shell.Shell;

public class MiniVM {

    public static void main(String[] args) {
        // create hardware
        Disk disk = new Disk();
        // create os
        SystemCall sysCall = new OperatingSystem(disk);
        // run shell
        new Shell(sysCall).run();
    }

}
