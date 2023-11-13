package visualizer.console;

import os.shell.Shell;
import visualizer.common.MiniVMColor;
import visualizer.common.MiniVMPanel;
import visualizer.common.MiniVMTextArea;
import visualizer.common.MiniVMTextField;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

public class Console extends MiniVMPanel {
    // model
    private Shell shell;
    // components
    private Queue<String> histories;
    private JTextArea output;
    private JTextField input;

    public Console(Shell shell) {
        super(new BorderLayout());
        // set associations
        this.shell = shell;
        // set components
        histories = new LinkedList<>();
        add(output = new MiniVMTextArea(1500, 180, false), BorderLayout.CENTER);
        add(input = new MiniVMTextField(1500, 30, e -> handleEnter()), BorderLayout.SOUTH);
    }

    private void addLine(String line) {
        String[] tokens = line.split("\n");
        histories.addAll(Arrays.asList(tokens));
        while (histories.size() > 10) histories.poll();
        String text = "";
        for (String history : histories) text += history + "\n";
        output.setText(text);
    }

    private void handleEnter() {
        String command = input.getText();
        addLine("$> " + command);
        addLine(shell.run(command));
        input.setText("");
    }

}
