package os.shell;

import os.OperatingSystem;

import static java.lang.System.exit;

public class Shell {

    private final OperatingSystem os;

    public Shell(OperatingSystem os) {
        this.os = os;
    }

    public String run(String command) {
        String message = "";
        if (command.isBlank()) return "";
        String[] argv = command.split(" ");
        switch (argv[0]) {
            case "mkdir" -> message = mkdir(argv);
            case "rmdir" -> message = rmdir(argv);
            case "ls" -> message = ls(argv);
            case "touch" -> message = touch(argv);
            case "rm" -> message = rm(argv);
            case "cd" -> message = cd(argv);
            case "save" -> message = save(argv);
            case "load" -> message = load(argv);
            case "exit" -> exit(0);
            default -> message = "command not found.";
        }
        return message;
    }

    private String load(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.createProcess(input[1]);
    }

    private String mkdir(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.mkdir(input[1]);
    }

    private String rmdir(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.rmdir(input[1]);
    }

    private String ls(String[] input) {
        if (input.length != 1) return "Invalid arguments.";
        return os.ls();
    }

    private String touch(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.touch(input[1]);
    }

    private String rm(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.rm(input[1]);
    }

    private String cd(String[] input) {
        if (input.length != 2) return "Invalid arguments.";
        return os.cd(input[1]);
    }

    private String save(String[] input) {
        if (input.length != 1) return "Invalid arguments.";
        return os.save();
    }

}
