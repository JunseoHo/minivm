package shell;

import os.SystemCall;
import os.file_manager.File;
import os.file_manager.FileType;

import java.util.Scanner;

public class Shell implements Runnable {

    private SystemCall systemCall;

    public Shell(SystemCall systemCall) {
        this.systemCall = systemCall;
    }

    @Override
    public void run() {
        System.out.println("Welcome to MiniVM.");
        while (true) {
            String[] input = new Scanner(System.in).nextLine().trim().split(" ");
            if (input == null || input.length < 1) {
                System.out.println("Invalid input.");
                continue;
            }
            switch (input[0]) {
                case "ls" -> ls(input);
                case "cat" -> cat(input);
                default -> System.out.println("Unknown command.");
            }
        }
    }

    private void cat(String[] parameters) {
        if (parameters.length != 2) {
            System.out.println("Invalid parameters.");
            return;
        }
        File file = systemCall.getFile(parameters[1]);
        if (file == null) System.out.println("file not found.");
        else if (file.type != FileType.DATA) System.out.println("file type is not data.");
        else {
            for (long l : file.records) System.out.print((char) l);
            System.out.println();
        }
    }

    private void ls(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Invalid parameters.");
            return;
        }
        File dir = systemCall.getCurrentDir();
        if (dir != null) System.out.println(dir);
        else System.err.println("Directory is null.");
    }

}
