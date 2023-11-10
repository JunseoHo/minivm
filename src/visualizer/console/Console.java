package visualizer.console;

import os.shell.Shell;
import visualizer.common.MiniVMColor;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

public class Console extends JPanel {
    // model
    private Shell shell;
    // components
    private Queue<String> histories;
    private JTextArea output;
    private JTextField input;

    public Console(Shell shell) {
        // set associations
        this.shell = shell;
        // set attributes
        setPreferredSize(new Dimension(1280, 260));
        setBackground(MiniVMColor.BACKGROUND);
        setLayout(new FlowLayout());
        // set components
        histories = new LinkedList<>();
        output = new JTextArea();
        output.setEditable(false);
        output.setEnabled(false);
        output.setPreferredSize(new Dimension(1800, 200));
        output.setBorder(new CompoundBorder(new LineBorder(MiniVMColor.BORDER, 2),
                new EmptyBorder(10, 10, 10, 10)));
        output.setDisabledTextColor(Color.LIGHT_GRAY);
        output.setBackground(MiniVMColor.AREA);
        add(output);
        input = new JTextField();
        input.setPreferredSize(new Dimension(1800, 30));
        input.setBorder(new CompoundBorder(new LineBorder(new Color(68, 70, 72), 2),
                new EmptyBorder(5, 5, 5, 5)));
        input.setForeground(Color.LIGHT_GRAY);
        input.setBackground(new Color(59, 62, 64));
        input.addActionListener(e -> handleEnter());
        add(input);
    }

    private void addLine(String line) {
        histories.add(line);
        if (histories.size() > 10) histories.poll();
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
