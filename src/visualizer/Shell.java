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
        consolePanel.add(stdout = new Console(new Color(0, 200, 0)));
        consolePanel.add(stderr = new Console(new Color(0, 200, 0)));
        add(consolePanel);
        stdin = new JTextField();
        stdin.setBackground(Color.BLACK);
        stdin.setFont(new Font("Arial", Font.PLAIN, 20));
        stdin.setForeground(new Color(0, 200, 0));
        stdin.setBorder(new LineBorder(Color.BLACK, 10));
        stdin.addActionListener(e -> {
            String commandLine = stdin.getText();
            stdout.addTextLine(commandLine);
            stdin.setText("");
            stderr.addTextLine(execute(commandLine));
        });
        add(stdin, BorderLayout.SOUTH);
    }

    private String execute(String commandLine) {
        String message = null;
        switch (commandLine) {
            case "exit" -> System.exit(0);
            default -> message = "command not found";
        }
        return message;
    }

}
