package os.shell;

import os.SystemCall;

import java.util.Scanner;

import static java.lang.System.exit;

public class Shell {

    private final SystemCall systemCall;

    public Shell(SystemCall systemCall) {
        this.systemCall = systemCall;
    }

    public void run() {
        while (true) {
            System.out.print("$> ");
            String[] input = new Scanner(System.in).nextLine().trim().split(" ");
            if (input.length < 1) continue;
            String message = "";
            switch (input[0]) {
                case "mkdir" -> message = mkdir(input);
                case "rmdir" -> message = rmdir(input);
                case "ls" -> message = ls(input);
                case "exit" -> exit(0);
                default -> message = "command not found.";
            }
            System.out.println(message);
        }
    }

    private String mkdir(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return systemCall.mkdir(input[1]);
    }

    private String rmdir(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return systemCall.rmdir(input[1]);
    }

    private String ls(String[] input) {
        if (input.length != 1) return "Invalid arguments.";
        return systemCall.ls();
    }

}
