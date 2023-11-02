package visualizer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

public class Console extends JTextArea {

    private static final int HISTORY_MAX_SIZE = 12;
    private final LinkedList<String> histories = new LinkedList<>();

    public Console() {
        this(MiniVMColor.GREEN);
    }

    public Console(Color borderColor) {
        setEditable(false);
        setEnabled(false);
        setPreferredSize(new Dimension(getWidth() / 2, 300));
        setBorder(new LineBorder(borderColor, 5));
        setDisabledTextColor(MiniVMColor.GREEN);
        setBackground(Color.BLACK);
        setFont(new Font("Consolas", Font.PLAIN, 20));
    }

    public void addTextLine(String textLine) {
        if (textLine == null) return;
        if (histories.size() == HISTORY_MAX_SIZE) histories.remove(0);
        histories.add(textLine);
        String text = "";
        for (String history : histories) text += history + "\n";
        setText(text);
    }
}
