package view;

import javax.swing.*;
import java.awt.*;

public class TextAreaPanel extends JScrollPane {
    // attributes
    private static final String FONT_NAME = "Consolas";
    private static final int FONT_STYLE = Font.PLAIN;
    private static final int FONT_SIZE = 15;
    // components
    protected JTextArea textArea;

    public TextAreaPanel() {
        this.textArea = new JTextArea();
        this.textArea.setBackground(Color.BLACK);
        this.textArea.setForeground(Color.WHITE);
        this.textArea.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        this.setViewportView(textArea);
    }

    public void appendString(String s) {
        this.textArea.append(s);
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }
}
