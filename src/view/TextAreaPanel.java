package view;

import javax.swing.*;
import java.awt.*;

public class TextAreaPanel extends JScrollPane {

    protected JTextArea textArea;

    public TextAreaPanel() {
        this.textArea = new JTextArea();
        this.textArea.setBackground(Color.BLACK);
        this.textArea.setForeground(Color.WHITE);
        this.textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        this.setViewportView(textArea);
    }

    public void appendString(String s) {
        this.textArea.append(s);
        this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
    }

}
