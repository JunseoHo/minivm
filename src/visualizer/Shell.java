package visualizer;

import os.SystemCall;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

public class Shell extends JPanel {
    // associations
    private final SystemCall systemCall;
    // components
    private JPanel consolePanel;
    private Console stdout;
    private Console stderr;
    private JTextField stdin;
    private Queue<String> inputQueue;

    public Shell(SystemCall systemCall) {
        // set associations
        this.systemCall = systemCall;
        // set attributes
        inputQueue = new LinkedList<>();
        setLayout(new BorderLayout());
        // add components
        consolePanel = new JPanel();
        consolePanel.setBackground(Color.BLACK);
        consolePanel.setLayout(new GridLayout(1, 2));
        consolePanel.add(stdout = new Console());
        consolePanel.add(stderr = new Console());
        add(consolePanel);
        stdin = new JTextField();
        stdin.setBackground(Color.BLACK);
        stdin.setFont(new Font("Arial", Font.PLAIN, 20));
        stdin.setForeground(MiniVMColor.GREEN);
        stdin.setBorder(new LineBorder(Color.BLACK, 10));
        stdin.setCaretColor(MiniVMColor.GREEN);
        stdin.addActionListener(e -> {
            stdout.addTextLine(stdin.getText());
            stderr.addTextLine(process(stdin.getText()));
            stdin.setText("");
        });
        add(stdin, BorderLayout.SOUTH);
    }

    private String process(String commandLine) {
        String message = null;
        String[] argv = commandLine.split(" ");
        if (argv.length < 1) return "invalid arguments";
        switch (argv[0]) {
            case "run" -> message = run(argv);
            case "exit" -> System.exit(0);
            default -> message = "command not found";
        }
        return message;
    }

    private String run(String[] argv) {
        return "run.";
    }

}
